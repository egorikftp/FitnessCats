package com.egoriku.catsrunning.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.AllFitnessDataAdapter;
import com.egoriku.catsrunning.fragments.AllFitnessDataFragment;
import com.egoriku.catsrunning.fragments.FitnessDataFragment;
import com.egoriku.catsrunning.fragments.LikedFragment;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.fragments.StatisticFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.analytics.FirebaseAnalytics.Event.LOGIN;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_EXIT_APP = "TAG_EXIT_APP";
    private static final String TAG_SETTING = "TAG_SETTING";
    public static final String BROADCAST_SAVE_NEW_TRACKS = "BROADCAST_SAVE_NEW_TRACKS";
    private static final String TRACKS = "tracks";
    private Toolbar toolbar;
    private Drawer result;

    private String emailText;
    private String nameText;

    private DatabaseReference mDatabase;
    private DatabaseReference updateDatabase;
    private List<AllFitnessDataAdapter> tracksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);

        setSupportActionBar(toolbar);
        tracksList = new ArrayList<>();

        if (savedInstanceState == null) {
            showFragment(AllFitnessDataFragment.newInstance(), FitnessDataFragment.TAG_MAIN_FRAGMENT, null, true);
        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailText = user.getEmail();
            nameText = user.getDisplayName();
        } else {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            finish();
        }

        createNavigationDrawer(savedInstanceState);

        if (getIntent().getExtras() != null && getIntent().getExtras().getString(RegisterActivity.KEY_LOGIN_EXTRA).equals(LOGIN)) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child(TRACKS).child(user.getUid());

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        tracksList.add(postSnapshot.getValue(AllFitnessDataAdapter.class));
                    }
                    saveSyncTracks();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("error:", databaseError.getMessage());
                }
            });
        }

        updateDatabase = FirebaseDatabase.getInstance().getReference().child(TRACKS).child(user.getUid());

        updateDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("child", "add");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e("child", "removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error:", databaseError.getMessage());
            }
        });
    }


    private void saveSyncTracks() {
        for (int i = 0; i < tracksList.size(); i++) {
            long idTrack = 0;
            SQLiteStatement statementTrack = App.getInstance().getDb().compileStatement(
                    "INSERT INTO Tracks (beginsAt, time, distance) VALUES (?, ?, ?)"
            );

            statementTrack.bindLong(1, tracksList.get(i).getBeginsAt());
            statementTrack.bindLong(2, tracksList.get(i).getTime());
            statementTrack.bindLong(3, tracksList.get(i).getDistance());

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


    public void onFragmentStart(int titleResId, String tag) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleResId);
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


    private void createNavigationDrawer(Bundle savedInstanceState) {
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(createAccountHeader())
                .addDrawerItems(
                        new PrimaryDrawerItem().
                                withIdentifier(1)
                                .withName(getString(R.string.navigation_drawer_main_activity))
                                .withIcon(getResources().getDrawable(R.drawable.ic_near_me_black))
                                .withTag(FitnessDataFragment.TAG_MAIN_FRAGMENT),
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
                        new PrimaryDrawerItem().
                                withIdentifier(4)
                                .withName(getString(R.string.navigation_drawer_statistic))
                                .withIcon(getResources().getDrawable(R.drawable.ic_equalizer_black))
                                .withTag(StatisticFragment.TAG_STATISTIC_FRAGMENT),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().
                                withIdentifier(5)
                                .withName(getString(R.string.navigation_drawer_exit))
                                .withIcon(getResources().getDrawable(R.drawable.ic_exit_to_app_black))
                                .withTag(TAG_EXIT_APP)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        String tag = String.valueOf(drawerItem.getTag());

                        if (tag.equals(FitnessDataFragment.TAG_MAIN_FRAGMENT)) {
                            showFragment(AllFitnessDataFragment.newInstance(), FitnessDataFragment.TAG_MAIN_FRAGMENT, null, true);
                        }

                        if (tag.equals(RemindersFragment.TAG_REMINDERS_FRAGMENT)) {
                            showFragment(RemindersFragment.newInstance(), RemindersFragment.TAG_REMINDERS_FRAGMENT, FitnessDataFragment.TAG_MAIN_FRAGMENT, false);
                        }

                        if (tag.equals(LikedFragment.TAG_LIKED_FRAGMENT)) {
                            showFragment(LikedFragment.newInstance(), LikedFragment.TAG_LIKED_FRAGMENT, FitnessDataFragment.TAG_MAIN_FRAGMENT, false);
                        }

                        if (tag.equals(TAG_EXIT_APP)) {
                            clearUserData();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
                            finish();
                        }

                        if (tag.equals(TAG_SETTING)) {
                            Toast.makeText(MainActivity.this, "Setting click", Toast.LENGTH_LONG).show();
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
                }).addStickyDrawerItems(
                        new SecondaryDrawerItem()
                                .withName(getString(R.string.navigation_drawer_setting))
                                .withIcon(getResources().getDrawable(R.drawable.ic_settings_black))
                                .withIdentifier(6)
                                .withTag(TAG_SETTING)
                )
                .withSavedInstance(savedInstanceState)
                .build();
    }


    private AccountHeader createAccountHeader() {
        return new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .addProfiles(
                        new ProfileDrawerItem().withName(nameText).withEmail(emailText).withIcon(getResources().getDrawable(R.mipmap.ic_launcher)))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Log.e("Click Account", "+");
                        return false;
                    }
                })
                .build();
    }


    private void clearUserData() {
        App.getInstance().getDb().execSQL("DELETE FROM User");
        App.getInstance().getDb().execSQL("DELETE FROM Tracks");
        App.getInstance().getDb().execSQL("DELETE FROM Reminder");
        App.getInstance().getDb().execSQL("DELETE FROM Point");
    }
}
