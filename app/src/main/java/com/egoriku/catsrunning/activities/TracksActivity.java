package com.egoriku.catsrunning.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.NavigationDrawerAdapter;
import com.egoriku.catsrunning.adapters.interfaces.OnItemSelecteListener;
import com.egoriku.catsrunning.fragments.AllFitnessDataFragment;
import com.egoriku.catsrunning.fragments.FitnessDataFragment;
import com.egoriku.catsrunning.fragments.LikedFragment;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.fragments.StatisticFragment;
import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.models.AllFitnessDataModel;
import com.egoriku.catsrunning.models.ItemNavigationDrawer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.models.State.BEGINS_AT;
import static com.egoriku.catsrunning.models.State.DISTANCE;
import static com.egoriku.catsrunning.models.State.LAT;
import static com.egoriku.catsrunning.models.State.LNG;
import static com.egoriku.catsrunning.models.State.TABLE_POINT;
import static com.egoriku.catsrunning.models.State.TABLE_TRACKS;
import static com.egoriku.catsrunning.models.State.TIME;
import static com.egoriku.catsrunning.models.State.TRACK_ID;
import static com.egoriku.catsrunning.models.State.TRACK_TOKEN;
import static com.egoriku.catsrunning.models.State.TYPE_FIT;

public class TracksActivity extends AppCompatActivity {
    public static final String BROADCAST_SAVE_NEW_TRACKS = "BROADCAST_SAVE_NEW_TRACKS";
    private static final String TAG_EXIT_APP = "TAG_EXIT_APP";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private RelativeLayout relativeLayoutSetting;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<ItemNavigationDrawer> drawerArrayList;

    private String emailText;
    private String nameText;
    private List<Long> localeDbDate;

    private NavigationDrawerAdapter adapter;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerArrayList = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_naw_drawer);
        relativeLayoutSetting = (RelativeLayout) findViewById(R.id.relative_layout_setting);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailText = user.getEmail();
            nameText = user.getDisplayName();
        } else {
            startActivity(new Intent(TracksActivity.this, RegisterActivity.class));
            finish();
        }

        addDrawerItem();

        initRecyclerView();

        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            showFragment(AllFitnessDataFragment.newInstance(), FitnessDataFragment.TAG_MAIN_FRAGMENT, null, true);
        }

        if (App.getInstance().getState() == null) {
            App.getInstance().createState();
        }


        App.getInstance().getTracksReference().child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("onChildAdded", "+");
                saveSyncTracks(dataSnapshot.getValue(AllFitnessDataModel.class));
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        relativeLayoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
                Toast.makeText(App.getInstance(), "Setting Click", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getLocaleTracksBeginsAt() {
        localeDbDate = new ArrayList<>();
        Cursor cursor = new InquiryBuilder()
                .get(BEGINS_AT)
                .from(TABLE_TRACKS)
                .select();

        DbCursor dbCursor = new DbCursor(cursor);
        if (dbCursor.isValid()) {
            do {
                localeDbDate.add(dbCursor.getLong(BEGINS_AT));
            } while (cursor.moveToNext());
        }
        dbCursor.close();
    }


    private void saveSyncTracks(AllFitnessDataModel someData) {
        getLocaleTracksBeginsAt();
        if (!localeDbDate.contains(someData.getBeginsAt())) {
            long idTrack = new InquiryBuilder()
                    .table(TABLE_TRACKS)
                    .set(BEGINS_AT, someData.getBeginsAt())
                    .set(TIME, someData.getTime())
                    .set(DISTANCE, someData.getDistance())
                    .set(TRACK_TOKEN, someData.getTrackToken())
                    .set(TYPE_FIT, someData.getTypeFit())
                    .insertForId(App.getInstance().getDb());

            for (int j = 0; j < someData.getPoints().size(); j++) {
                new InquiryBuilder()
                        .table(TABLE_POINT)
                        .set(LAT, someData.getPoints().get(j).getLat())
                        .set(LNG, someData.getPoints().get(j).getLng())
                        .set(TRACK_ID, idTrack)
                        .insert(App.getInstance().getDb());
            }
        }
        localeDbDate.clear();
        LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcastSync(new Intent(BROADCAST_SAVE_NEW_TRACKS));
    }


    private void showFragment(Fragment fragment, String tag, String clearToTag, boolean clearInclusive) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (clearToTag != null || clearInclusive) {
            fragmentManager.popBackStack(
                    clearToTag,
                    clearInclusive ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0
            );
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        drawerToggle.setDrawerIndicatorEnabled(true);
        super.onBackPressed();
    }


    public void onFragmentStart(int titleResId, String tag) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleResId);
        }

        for (int i = 1; i < drawerArrayList.size(); i++) {
            if (drawerArrayList.get(i).getTagFragment().equals(tag)) {
                drawerArrayList.get(i).setSelected(true);
                adapter.notifyDataSetChanged();
                continue;
            }
            drawerArrayList.get(i).setSelected(false);
        }
    }

    private void addDrawerItem() {
        drawerArrayList.add(new ItemNavigationDrawer(nameText, emailText));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_main_activity),
                R.drawable.ic_vec_near_me_black,
                FitnessDataFragment.TAG_MAIN_FRAGMENT,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_reminders),
                R.drawable.ic_vec_notifications_active_black,
                RemindersFragment.TAG_REMINDERS_FRAGMENT,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_liked),
                R.drawable.ic_vec_star_black_nav_drawer,
                LikedFragment.TAG_LIKED_FRAGMENT,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_statistic),
                R.drawable.ic_vec_equalizer_black,
                StatisticFragment.TAG_STATISTIC_FRAGMENT,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_exit),
                R.drawable.ic_vec_exit_to_app_black,
                TAG_EXIT_APP,
                false,
                true
        ));
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NavigationDrawerAdapter(drawerArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.hasFixedSize();

        adapter.setOnItemSelecteListener(new OnItemSelecteListener() {
            @Override
            public void onItemSelected(View v, int position) {
                if (drawerArrayList.get(position).getTagFragment() != null) {
                    changeNavigationDrawerItem(drawerArrayList.get(position).getTagFragment());
                }
            }
        });
    }

    private void changeNavigationDrawerItem(String tag) {
        if (tag.equals(FitnessDataFragment.TAG_MAIN_FRAGMENT)) {
            showFragment(AllFitnessDataFragment.newInstance(), FitnessDataFragment.TAG_MAIN_FRAGMENT, null, true);
            drawerLayout.closeDrawers();
        }

        if (tag.equals(RemindersFragment.TAG_REMINDERS_FRAGMENT)) {
            showFragment(RemindersFragment.newInstance(), RemindersFragment.TAG_REMINDERS_FRAGMENT, FitnessDataFragment.TAG_MAIN_FRAGMENT, false);
            drawerLayout.closeDrawers();
        }

        if (tag.equals(LikedFragment.TAG_LIKED_FRAGMENT)) {
            showFragment(LikedFragment.newInstance(), LikedFragment.TAG_LIKED_FRAGMENT, FitnessDataFragment.TAG_MAIN_FRAGMENT, false);
            drawerLayout.closeDrawers();
        }

        if (tag.equals(StatisticFragment.TAG_STATISTIC_FRAGMENT)) {
            showFragment(StatisticFragment.newInstance(), StatisticFragment.TAG_STATISTIC_FRAGMENT, FitnessDataFragment.TAG_MAIN_FRAGMENT, false);
            drawerLayout.closeDrawers();
        }

        if (tag.equals(TAG_EXIT_APP)) {
            drawerLayout.closeDrawers();
            clearUserData();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(TracksActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
            finish();
        }
    }


    private void clearUserData() {
        App.getInstance().getDb().execSQL("DELETE FROM User");
        App.getInstance().getDb().execSQL("DELETE FROM Tracks");
        App.getInstance().getDb().execSQL("DELETE FROM Reminder");
        App.getInstance().getDb().execSQL("DELETE FROM Point");
    }
}
