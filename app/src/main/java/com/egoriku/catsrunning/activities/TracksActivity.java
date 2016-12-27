package com.egoriku.catsrunning.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.NavigationDrawerAdapter;
import com.egoriku.catsrunning.adapters.interfaces.OnItemSelecteListener;
import com.egoriku.catsrunning.fragments.AllFitnessDataFragment;
import com.egoriku.catsrunning.fragments.LikedFragment;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.fragments.StatisticFragment;
import com.egoriku.catsrunning.helpers.FirebaseSync;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.models.ItemNavigationDrawer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_POINT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_REMINDER;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_USER;
import static com.egoriku.catsrunning.models.Constants.Tags.TAG_EXIT_APP;
import static com.egoriku.catsrunning.models.Constants.Tags.TAG_LIKED_FRAGMENT;
import static com.egoriku.catsrunning.models.Constants.Tags.TAG_MAIN_FRAGMENT;
import static com.egoriku.catsrunning.models.Constants.Tags.TAG_REMINDERS_FRAGMENT;
import static com.egoriku.catsrunning.models.Constants.Tags.TAG_STATISTIC_FRAGMENT;

public class TracksActivity extends AppCompatActivity {
    public static final long UPTIME_MILLIS = 1000L;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private RelativeLayout relativeLayoutSetting;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private List<ItemNavigationDrawer> drawerArrayList;

    private String emailText;
    private String nameText;

    private NavigationDrawerAdapter adapter;
    private FirebaseUser user;
    private FirebaseSync firebaseSync;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        drawerLayout = (DrawerLayout) findViewById(R.id.tracks_activity_drawer_layout);
        recyclerView = (RecyclerView) findViewById(R.id.tracks_activity_recycler_view_nav_drawer);
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
            showFragment(AllFitnessDataFragment.newInstance(), TAG_MAIN_FRAGMENT, null, true);
        }

        firebaseSync = FirebaseSync.getInstance();
        handler = new Handler(getMainLooper());
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                firebaseSync.startSync(user);
            }
        }, UPTIME_MILLIS);

        relativeLayoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
                Toast.makeText(App.getInstance(), "Setting Click", Toast.LENGTH_LONG).show();
            }
        });
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
        transaction.replace(R.id.tracks_activity_fragment_container, fragment, tag);
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
        drawerArrayList = new ArrayList<>();
        drawerArrayList.add(new ItemNavigationDrawer(nameText, emailText));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_main_activity),
                R.drawable.ic_vec_near_me_black,
                TAG_MAIN_FRAGMENT,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_reminders),
                R.drawable.ic_vec_notifications_active_black,
                TAG_REMINDERS_FRAGMENT,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_liked),
                R.drawable.ic_vec_star_black_nav_drawer,
                TAG_LIKED_FRAGMENT,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_statistic),
                R.drawable.ic_vec_equalizer_black,
                TAG_STATISTIC_FRAGMENT,
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
        if (tag.equals(TAG_MAIN_FRAGMENT)) {
            showFragment(AllFitnessDataFragment.newInstance(), TAG_MAIN_FRAGMENT, null, true);
            drawerLayout.closeDrawers();
        }

        if (tag.equals(TAG_REMINDERS_FRAGMENT)) {
            showFragment(RemindersFragment.newInstance(), TAG_REMINDERS_FRAGMENT, TAG_MAIN_FRAGMENT, false);
            drawerLayout.closeDrawers();
        }

        if (tag.equals(TAG_LIKED_FRAGMENT)) {
            showFragment(LikedFragment.newInstance(), TAG_LIKED_FRAGMENT, TAG_MAIN_FRAGMENT, false);
            drawerLayout.closeDrawers();
        }

        if (tag.equals(TAG_STATISTIC_FRAGMENT)) {
            showFragment(StatisticFragment.newInstance(), TAG_STATISTIC_FRAGMENT, TAG_MAIN_FRAGMENT, false);
            drawerLayout.closeDrawers();
        }

        if (tag.equals(TAG_EXIT_APP)) {
            if (App.getInstance().getFitState() == null) {
                exitFromAccount();
            } else if (App.getInstance().getFitState() != null && !App.getInstance().getFitState().isFitRun()) {
                exitFromAccount();
            } else if (App.getInstance().getFitState().isFitRun())
                Toast.makeText(TracksActivity.this, getString(R.string.tracks_activity_error_exit_account), Toast.LENGTH_SHORT).show();
        }
    }

    private void exitFromAccount() {
        clearUserData();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(TracksActivity.this, RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
        finish();
    }


    private void clearUserData() {
        new InquiryBuilder().cleanTable(TABLE_USER);
        new InquiryBuilder().cleanTable(TABLE_TRACKS);
        new InquiryBuilder().cleanTable(TABLE_REMINDER);
        new InquiryBuilder().cleanTable(TABLE_POINT);
    }


    public void tabTitle(String titleId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleId);
        }
    }
}
