package com.egoriku.catsrunning.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.fragments.AllFitnessDataFragment;
import com.egoriku.catsrunning.fragments.FragmentsTag;
import com.egoriku.catsrunning.fragments.LikedFragment;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.fragments.StatisticFragment;
import com.egoriku.catsrunning.fragments.WhereIFragment;
import com.egoriku.catsrunning.models.FitState;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import static com.egoriku.catsrunning.fragments.FragmentsTag.MAIN;

public class TracksActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private Toolbar toolbar;
    //private DrawerLayout drawerLayout;
    //private RelativeLayout relativeLayoutSetting;
    //  private ActionBarDrawerToggle drawerToggle;
    // private List<ItemNavigationDrawer> drawerArrayList;

    private String userEmail;
    private String userName;
    private Uri userPhoto;

    // private NavigationDrawerAdapter adapter;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FitState fitState = FitState.getInstance();

    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        // drawerLayout = (DrawerLayout) findViewById(R.id.tracks_activity_drawer_layout);
        //  recyclerView = (RecyclerView) findViewById(R.id.tracks_activity_recycler_view_nav_drawer);
        // relativeLayoutSetting = (RelativeLayout) findViewById(R.id.relative_layout_setting);

        checkUserLogin();
        //FirebaseUserInfoSync.getInstance().startSync(user, this);

        initDrawer(savedInstanceState);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // drawerLayout.addDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            showFragment(AllFitnessDataFragment.newInstance(), MAIN, null, true);
        }

      /*  relativeLayoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
                Toast.makeText(App.getInstance(), "Setting", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void checkUserLogin() {
        if (user != null) {
            userEmail = user.getEmail();
            userName = user.getDisplayName();
            userPhoto = user.getPhotoUrl();
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

    public void onFragmentStart(int titleResId, @FragmentsTag String tag) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleResId);
        }
    }

    private void initDrawer(Bundle savedInstanceState) {
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(getDrawerHeader())
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_main_activity)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_vec_tracks))
                                .withTag(MAIN),
                        new PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_reminders)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_vec_notifications))
                                .withTag(FragmentsTag.REMINDER),
                        new PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_liked)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_vec_favorite))
                                .withTag(FragmentsTag.LIKED),
                        new PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_where_i)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_vec_pin_location))
                                .withTag(FragmentsTag.WHERE_I),
                        new PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_statistic)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_vec_statistic))
                                .withTag(FragmentsTag.STATISTIC),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName(R.string.navigation_drawer_exit)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_vec_exit_from_app))
                                .withTag(FragmentsTag.EXIT))
                .addStickyDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_setting)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_vec_settings))
                                .withTag(FragmentsTag.SETTINGS))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        String tag = String.valueOf(drawerItem.getTag());

                        switch (tag) {
                            case FragmentsTag.MAIN:
                                showFragment(AllFitnessDataFragment.newInstance(), FragmentsTag.MAIN, null, true);
                                break;
                            case FragmentsTag.REMINDER:
                                showFragment(RemindersFragment.newInstance(), FragmentsTag.REMINDER, FragmentsTag.MAIN, false);
                                break;
                            case FragmentsTag.LIKED:
                                showFragment(LikedFragment.newInstance(), FragmentsTag.LIKED, FragmentsTag.MAIN, false);
                                break;
                            case FragmentsTag.WHERE_I:
                                showFragment(WhereIFragment.newInstance(), FragmentsTag.WHERE_I, FragmentsTag.MAIN, false);
                                break;
                            case FragmentsTag.STATISTIC:
                                showFragment(StatisticFragment.newInstance(), FragmentsTag.STATISTIC, FragmentsTag.MAIN, false);
                                break;
                            case FragmentsTag.EXIT:
                                if (fitState.isFitRun()) {
                                    Toast.makeText(TracksActivity.this, R.string.tracks_activity_error_exit_account, Toast.LENGTH_SHORT).show();
                                } else {
                                    exitFromAccount();
                                }
                                break;
                            case FragmentsTag.SETTINGS:
                                break;
                        }
                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }

    private AccountHeader getDrawerHeader() {
        return new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.primary_dark)
                .addProfiles(new ProfileDrawerItem()
                        .withName(userName)
                        .withEmail(userEmail)
                        .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_vec_cats_weary)))
                .build();
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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawer.getActionBarDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawer.getActionBarDrawerToggle().onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawer.getActionBarDrawerToggle().onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        super.onBackPressed();
    }

}
