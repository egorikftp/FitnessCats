package com.egoriku.catsrunning.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.AllFitnessDataAdapter;
import com.egoriku.catsrunning.adapters.NavigationDrawerAdapter;
import com.egoriku.catsrunning.adapters.interfaces.OnItemSelecteListener;
import com.egoriku.catsrunning.fragments.AllFitnessDataFragment;
import com.egoriku.catsrunning.fragments.FitnessDataFragment;
import com.egoriku.catsrunning.fragments.LikedFragment;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.fragments.StatisticFragment;
import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.models.ItemNavigationDrawer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.models.State.BEGINS_AT_EQ;
import static com.egoriku.catsrunning.models.State.TABLE_TRACKS;
import static com.egoriku.catsrunning.models.State._ID;

public class TracksActivity extends AppCompatActivity {
    private static final String TAG_EXIT_APP = "TAG_EXIT_APP";
    public static final String BROADCAST_SAVE_NEW_TRACKS = "BROADCAST_SAVE_NEW_TRACKS";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private LinearLayout linearLayoutSetting;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<ItemNavigationDrawer> drawerArrayList;

    private String emailText;
    private String nameText;
    private List<AllFitnessDataAdapter> tracksList;
    private NavigationDrawerAdapter adapter;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerArrayList = new ArrayList<>();
        tracksList = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_naw_drawer);
        linearLayoutSetting = (LinearLayout) findViewById(R.id.linear_layout_setting);

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

        App.getInstance().getTracksReference().child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("child", "first sync2");
                if (App.getInstance().getState().isLogin()) {
                    Log.e("child", "first sync3");
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        tracksList.add(postSnapshot.getValue(AllFitnessDataAdapter.class));
                    }
                    saveSyncTracks();
                    App.getInstance().getState().setLogin(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        App.getInstance().getTracksReference().child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("child", "add");
                Log.e("child add", "second sync");
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int idTrack = 0;
                Cursor cursor = new InquiryBuilder()
                        .get(_ID)
                        .from(TABLE_TRACKS)
                        .where(false, BEGINS_AT_EQ, String.valueOf(dataSnapshot.getValue(AllFitnessDataAdapter.class).getBeginsAt()))
                        .select();

                DbCursor dbCursor = new DbCursor(cursor);
                if (dbCursor.isValid()) {
                    idTrack = dbCursor.getInt(_ID);
                }
                dbCursor.close();

                SQLiteStatement statementPoints = App.getInstance().getDb().compileStatement(
                        "DELETE FROM Point WHERE Point.trackId = ?");

                statementPoints.bindLong(1, idTrack);
                try {
                    statementPoints.executeUpdateDelete();
                } finally {
                    statementPoints.close();
                }

                SQLiteStatement statement = App.getInstance().getDb().compileStatement("DELETE FROM Tracks WHERE Tracks._id = ?");
                statement.bindLong(1, idTrack);
                try {
                    statement.execute();
                } finally {
                    statement.close();
                }
                LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcastSync(new Intent(BROADCAST_SAVE_NEW_TRACKS));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error:", databaseError.getMessage());
            }
        });

        linearLayoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
                Toast.makeText(App.getInstance(), "Setting Click", Toast.LENGTH_LONG).show();
            }
        });
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

        if (tag.equals(TAG_EXIT_APP)) {
            drawerLayout.closeDrawers();
            clearUserData();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(TracksActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
            finish();
        }
    }


    private void saveSyncTracks() {
        for (int i = 0; i < tracksList.size(); i++) {
            long idTrack = 0;
            SQLiteStatement statementTrack = App.getInstance().getDb().compileStatement(
                    "INSERT INTO Tracks (beginsAt, time, distance, trackToken, typeFit) VALUES (?, ?, ?, ?, ?)"
            );

            statementTrack.bindLong(1, tracksList.get(i).getBeginsAt());
            statementTrack.bindLong(2, tracksList.get(i).getTime());
            statementTrack.bindLong(3, tracksList.get(i).getDistance());
            statementTrack.bindString(4, tracksList.get(i).getTrackToken());
            statementTrack.bindLong(5, tracksList.get(i).getTypeFit());

            try {
                idTrack = statementTrack.executeInsert();
                Log.e("id", String.valueOf(idTrack));
            } finally {
                statementTrack.close();
            }

            for (int j = 0; j < tracksList.get(i).getPoints().size(); j++) {
                SQLiteStatement statementPoints = App.getInstance().getDb().compileStatement(
                        "INSERT INTO Point (latitude, longitude, trackId) VALUES (?, ?, ?)"
                );

                statementPoints.bindDouble(1, tracksList.get(i).getPoints().get(j).getLat());
                statementPoints.bindDouble(2, tracksList.get(i).getPoints().get(j).getLng());
                statementPoints.bindLong(3, idTrack);

                try {
                    statementPoints.execute();
                    Log.e("execute", "points");
                } finally {
                    statementPoints.close();
                }
            }
        }
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


    private void clearUserData() {
        App.getInstance().getDb().execSQL("DELETE FROM User");
        App.getInstance().getDb().execSQL("DELETE FROM Tracks");
        App.getInstance().getDb().execSQL("DELETE FROM Reminder");
        App.getInstance().getDb().execSQL("DELETE FROM Point");
    }
}
