package com.egoriku.catsrunning.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.egoriku.catsrunning.fragments.FragmentsTag;
import com.egoriku.catsrunning.fragments.LikedFragment;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.fragments.StatisticFragment;
import com.egoriku.catsrunning.fragments.WhereIFragment;
import com.egoriku.catsrunning.models.FitState;
import com.egoriku.catsrunning.models.ItemNavigationDrawer;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class TracksActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private RelativeLayout relativeLayoutSetting;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private List<ItemNavigationDrawer> drawerArrayList;

    private String emailText;
    private String nameText;

    private NavigationDrawerAdapter adapter;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FitState fitState = FitState.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        drawerLayout = (DrawerLayout) findViewById(R.id.tracks_activity_drawer_layout);
        recyclerView = (RecyclerView) findViewById(R.id.tracks_activity_recycler_view_nav_drawer);
        relativeLayoutSetting = (RelativeLayout) findViewById(R.id.relative_layout_setting);

        checkUserLogin();
        //FirebaseUserInfoSync.getInstance().startSync(user, this);

        addDrawerItem();
        initRecyclerView();

        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            showFragment(AllFitnessDataFragment.newInstance(), FragmentsTag.MAIN, null, true);
        }

        relativeLayoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
                Toast.makeText(App.getInstance(), "Setting", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserLogin() {
        if (user != null) {
            emailText = user.getEmail();
            nameText = user.getDisplayName();
        } else {
            startActivity(new Intent(TracksActivity.this, RegisterActivity.class));
            finish();
        }
    }

    private void showFragment(Fragment fragment, @FragmentsTag String tag, @FragmentsTag String clearToTag, boolean clearInclusive) {
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

    public void onFragmentStart(int titleResId, @FragmentsTag String tag) {
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
                FragmentsTag.MAIN,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_reminders),
                R.drawable.ic_vec_notifications_active_black,
                FragmentsTag.REMINDER,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_liked),
                R.drawable.ic_vec_star_black_nav_drawer,
                FragmentsTag.LIKED,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_where_i),
                R.drawable.ic_vec_person_pin_black,
                FragmentsTag.WHERE_I,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_statistic),


                R.drawable.ic_vec_equalizer_black,
                FragmentsTag.STATISTIC,
                false,
                false
        ));

        drawerArrayList.add(new ItemNavigationDrawer(
                getString(R.string.navigation_drawer_exit),
                R.drawable.ic_vec_exit_to_app_black,
                FragmentsTag.EXIT,
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

    private void changeNavigationDrawerItem(@FragmentsTag String tag) {
        switch (tag) {
            case FragmentsTag.MAIN:
                showFragment(AllFitnessDataFragment.newInstance(), FragmentsTag.MAIN, null, true);
                drawerLayout.closeDrawers();
                break;
            case FragmentsTag.REMINDER:
                showFragment(RemindersFragment.newInstance(), FragmentsTag.REMINDER, FragmentsTag.MAIN, false);
                drawerLayout.closeDrawers();
                break;
            case FragmentsTag.LIKED:
                showFragment(LikedFragment.newInstance(), FragmentsTag.LIKED, FragmentsTag.MAIN, false);
                drawerLayout.closeDrawers();
                break;
            case FragmentsTag.WHERE_I:
                showFragment(WhereIFragment.newInstance(), FragmentsTag.WHERE_I, FragmentsTag.MAIN, false);
                drawerLayout.closeDrawers();
                break;
            case FragmentsTag.STATISTIC:
                showFragment(StatisticFragment.newInstance(), FragmentsTag.STATISTIC, FragmentsTag.MAIN, false);
                drawerLayout.closeDrawers();
                break;
            case FragmentsTag.EXIT:
                if (fitState.isFitRun()) {
                    Toast.makeText(TracksActivity.this, getString(R.string.tracks_activity_error_exit_account), Toast.LENGTH_SHORT).show();
                } else {
                    exitFromAccount();
                }
                break;
        }
    }

    private void exitFromAccount() {
        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (googleApiClient.isConnected()) {
            Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    openRegisterActivity();
                }
            });
        }
    }

    private void openRegisterActivity() {
        startActivity(new Intent(TracksActivity.this, RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
        finish();
    }

    public void tabTitle(String titleId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleId);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
