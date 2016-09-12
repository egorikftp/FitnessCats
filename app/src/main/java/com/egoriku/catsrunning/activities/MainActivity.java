package com.egoriku.catsrunning.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.fragments.LikedFragment;
import com.egoriku.catsrunning.fragments.RemindersFragment;
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

    private String emailText;
    private String nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textName = (TextView) findViewById(R.id.text_name);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        btnCancelConnection = (Button) findViewById(R.id.btn_close_connection);

        btnCancelConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
            }
        });

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

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemLocation,
                        itemReminder,
                        itemLiked,
                        itemExit
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Log.e("Click", String.valueOf(position));
                        return true;
                    }
                })
                .build();
    }
}
