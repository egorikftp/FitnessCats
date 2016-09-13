package com.egoriku.catsrunning.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.fragments.LikedFragment;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.fragments.TrackFragment;
import com.egoriku.catsrunning.fragments.TracksListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private TextView textName;
    private Toolbar toolbar;
    private Button btnCancelConnection;

    private Drawer result;

    private String emailText;
    private String nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textName = (TextView) findViewById(R.id.text_name);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        btnCancelConnection = (Button) findViewById(R.id.btn_close_connection);

        setSupportActionBar(toolbar);

        btnCancelConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
            }
        });

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

        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("egoriku");
        scoresRef.keepSynced(true);

        //инициализация Drawer Builder
        new DrawerBuilder().withActivity(this).build();

        PrimaryDrawerItem itemLocation = new PrimaryDrawerItem().
                withIdentifier(1)
                .withName(getString(R.string.navigation_drawer_main_activity))
                .withIcon(getResources().getDrawable(R.drawable.ic_location))
                .withTag(TracksListFragment.TAG_MAIN_FRAGMENT);

        PrimaryDrawerItem itemReminder = new PrimaryDrawerItem().
                withIdentifier(2)
                .withName(getString(R.string.navigation_drawer_reminders))
                .withIcon(getResources().getDrawable(R.drawable.ic_notifications))
                .withTag(RemindersFragment.TAG_REMINDERS_FRAGMENT);

        PrimaryDrawerItem itemLiked = new PrimaryDrawerItem().
                withIdentifier(3)
                .withName(getString(R.string.navigation_drawer_liked))
                .withIcon(getResources().getDrawable(R.drawable.ic_star_black))
                .withTag(LikedFragment.TAG_LIKED_FRAGMENT);

        SecondaryDrawerItem itemExit = new SecondaryDrawerItem().
                withIdentifier(4)
                .withName(getString(R.string.navigation_drawer_exit))
                .withIcon(getResources().getDrawable(R.drawable.ic_exit_to_app))
                .withTag(TAG_EXIT_APP);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.side_nav_bar)
                .addProfiles(
                        new ProfileDrawerItem().withName(nameText).withEmail(emailText).withIcon(getResources().getDrawable(R.drawable.panda_navigation_drawer))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Log.e("Click Account", "+");
                        return false;
                    }
                })
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemLocation,
                        itemReminder,
                        itemLiked,
                        new DividerDrawerItem(),
                        itemExit
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
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
                        return true;
                    }
                })
                .build();

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


    public void onFragmentStart(int titleResId, String tag) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleResId);
        }

        for (int i = 0; i < result.getDrawerItems().size(); i++) {
            if (result.getDrawerItems().get(i).getTag().equals(tag)) {
                result.setSelection(i, true);
            }
        }

        if (tag.equals(TrackFragment.TAG_TRACK_FRAGMENT)) {
            for (int i = 0; i < result.getDrawerItems().size(); i++) {
                result.setSelection(i, false);
            }
        }
    }



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        result.getActionBarDrawerToggle().syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        result.getActionBarDrawerToggle().onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return  result.getActionBarDrawerToggle().onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if(result.getDrawerLayout().isDrawerVisible(GravityCompat.START)){
            result.getDrawerLayout().closeDrawer(GravityCompat.START);
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        super.onBackPressed();
    }
}
