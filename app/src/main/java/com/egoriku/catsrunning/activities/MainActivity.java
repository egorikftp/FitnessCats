package com.egoriku.catsrunning.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.fragments.LikedFragment;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.fragments.TrackFragment;
import com.egoriku.catsrunning.fragments.TracksListFragment;
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
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_EXIT_APP = "TAG_EXIT_APP";
    private Toolbar toolbar;
    private Drawer result;

    private String emailText;
    private String nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            showFragment(TracksListFragment.newInstance(), TracksListFragment.TAG_MAIN_FRAGMENT, null, true);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailText = user.getEmail();
            nameText = user.getDisplayName();
        } else {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            finish();
        }

        //  DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("egoriku");
        //scoresRef.keepSynced(true);

        //инициализация Drawer Builder

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .addProfiles(new ProfileDrawerItem().withName(nameText).withEmail(emailText).withIcon(getResources().getDrawable(R.mipmap.ic_launcher)))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Log.e("Click Account", "+");
                        return false;
                    }
                })
                .build();

        Log.e("AccountHeader", "+");
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().
                                withIdentifier(1)
                                .withName(getString(R.string.navigation_drawer_main_activity))
                                .withIcon(getResources().getDrawable(R.drawable.ic_edit_location_black))
                                .withTag(TracksListFragment.TAG_MAIN_FRAGMENT),
                        new PrimaryDrawerItem().
                                withIdentifier(2)
                                .withName(getString(R.string.navigation_drawer_reminders))
                                .withIcon(getResources().getDrawable(R.drawable.ic_notifications_black))
                                .withTag(RemindersFragment.TAG_REMINDERS_FRAGMENT),
                        new PrimaryDrawerItem().
                                withIdentifier(3)
                                .withName(getString(R.string.navigation_drawer_liked))
                                .withIcon(getResources().getDrawable(R.drawable.ic_star_black))
                                .withTag(LikedFragment.TAG_LIKED_FRAGMENT),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().
                                withIdentifier(4)
                                .withName(getString(R.string.navigation_drawer_exit))
                                .withIcon(getResources().getDrawable(R.drawable.ic_exit_to_app_black))
                                .withTag(TAG_EXIT_APP)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        String tag = String.valueOf(drawerItem.getTag());

                        if (tag.equals(TracksListFragment.TAG_MAIN_FRAGMENT)) {
                            showFragment(TracksListFragment.newInstance(), TracksListFragment.TAG_MAIN_FRAGMENT, null, true);
                        }

                        if (tag.equals(RemindersFragment.TAG_REMINDERS_FRAGMENT)) {
                            showFragment(RemindersFragment.newInstance(), RemindersFragment.TAG_REMINDERS_FRAGMENT, TracksListFragment.TAG_MAIN_FRAGMENT, false);
                        }

                        if (tag.equals(LikedFragment.TAG_LIKED_FRAGMENT)) {
                            showFragment(LikedFragment.newInstance(), LikedFragment.TAG_LIKED_FRAGMENT, TracksListFragment.TAG_MAIN_FRAGMENT, false);
                        }

                        if (tag.equals(TAG_EXIT_APP)) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
                            finish();
                        }
                        return false;
                    }
                }).withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        //Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }
                })
                .build();
        Log.e("Build Drawer", "+++");
    }


    private void showFragment(Fragment fragment, String tag, String clearToTag, boolean clearInclusive) {
        Log.e("showFragment", "+");
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


    public void onFragmentStart(int titleResId, String tag) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleResId);
        }

        if (tag.equals(TrackFragment.TAG_TRACK_FRAGMENT)) {
            result.setSelection(-1);
        }
    }


    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        super.onBackPressed();
    }
}
