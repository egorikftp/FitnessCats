package com.egoriku.catsrunning.activities;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.fragments.AllFitnessDataFragment;
import com.egoriku.catsrunning.fragments.FragmentsTag;
import com.egoriku.catsrunning.fragments.LikedFragment;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.fragments.SettingsFragment;
import com.egoriku.catsrunning.fragments.StatisticFragment;
import com.egoriku.catsrunning.models.FitState;
import com.egoriku.catsrunning.utils.FirebaseUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;
import static com.egoriku.catsrunning.activities.SplashActivity.Constant.IS_ANIMATE;
import static com.egoriku.catsrunning.fragments.FragmentsTag.MAIN;

public class TracksActivity extends AppCompatActivity {

    public static final String NAV_DRAWER_SELECTED_POSITION = "Nav_drawer_position";
    private Drawer navigationDrawer;
    private Toolbar toolbar;

    private String userEmail;
    private String userName;
    private Uri userPhoto;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FitState fitState = FitState.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUtils.updateUserInfo(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        checkUserLogin();

        initDrawer(savedInstanceState);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            showFragment(AllFitnessDataFragment.newInstance(), MAIN, null, true);
        }
    }

    private void checkUserLogin() {
        if (user != null) {
            userEmail = user.getEmail();
            userName = user.getDisplayName();
            userPhoto = user.getPhotoUrl();
        } else {
            openLoginActivity();
        }
    }

    private void initDrawer(Bundle savedInstanceState) {
        navigationDrawer = new DrawerBuilder()
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
                        setDefaultToolbarColor();
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
                                showFragment(SettingsFragment.newInstance(), FragmentsTag.SETTINGS, FragmentsTag.MAIN, false);
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
                        .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_vec_cat_weary)))
                .build();
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


    private void exitFromAccount() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        openLoginActivity();
                    }
                });
    }

    public void onFragmentStart(int titleResId, @FragmentsTag String tag) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleResId);
        }
    }

    private void openLoginActivity() {
        Intent intent = new Intent(TracksActivity.this, SplashActivity.class);
        intent.putExtra(IS_ANIMATE, true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
        finish();
    }

    public void tabTitle(String titleId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleId);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        navigationDrawer.getActionBarDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navigationDrawer.getActionBarDrawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return navigationDrawer.getActionBarDrawerToggle().onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawer != null && navigationDrawer.isDrawerOpen()) {
            navigationDrawer.closeDrawer();
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        super.onBackPressed();
    }

    public void animateToolbar(@ColorRes final int colorAccent, @ColorRes final int colorPrimaryDark) {
        final int cx = toolbar.getWidth() / 2;
        final int cy = toolbar.getHeight() / 2;
        final float finalRadius = (float) Math.hypot(cx, cy);

        toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(toolbar, cx, cy, 0, finalRadius);
                    toolbar.setBackgroundColor(ContextCompat.getColor(TracksActivity.this, colorPrimaryDark));
                    circularReveal.start();
                    getWindow().setStatusBarColor(ContextCompat.getColor(TracksActivity.this, colorPrimaryDark));
                    toolbar.setBackgroundColor(ContextCompat.getColor(TracksActivity.this, colorAccent));
                } else {
                    toolbar.setBackgroundColor(ContextCompat.getColor(TracksActivity.this, colorAccent));
                }
            }
        });
    }

    private void setDefaultToolbarColor() {
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.settings_toolbar_color));

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.settings_toolbar_color_dark));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_DRAWER_SELECTED_POSITION, navigationDrawer.getCurrentSelectedPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationDrawer.setSelectionAtPosition(savedInstanceState.getInt(NAV_DRAWER_SELECTED_POSITION));
    }
}
