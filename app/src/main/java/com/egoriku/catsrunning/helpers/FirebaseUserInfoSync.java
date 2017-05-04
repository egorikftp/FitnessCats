package com.egoriku.catsrunning.helpers;

import android.content.Context;

import com.egoriku.catsrunning.activities.AddUserInfoActivity;
import com.egoriku.catsrunning.models.UserInfoModel;
import com.egoriku.catsrunning.utils.IntentBuilder;
import com.egoriku.catsrunning.utils.UserInfoPreferences;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.models.Constants.FirebaseFields.USER_INFO;


public class FirebaseUserInfoSync {
    private static FirebaseUserInfoSync firebaseUserInfoSync;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public static FirebaseUserInfoSync getInstance() {
        if (firebaseUserInfoSync == null) {
            firebaseUserInfoSync = new FirebaseUserInfoSync();
        }
        return firebaseUserInfoSync;
    }

    public void startSync(final FirebaseUser user, final Context context) {
        firebaseDatabase.getReference().child(USER_INFO).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    context.startActivity(new IntentBuilder()
                            .context(context)
                            .activity(AddUserInfoActivity.class)
                            .build()
                    );
                } else {
                    UserInfoPreferences userInfoPreferences = new UserInfoPreferences(context);
                    List<UserInfoModel> userInfo = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userInfo.add(snapshot.getValue(UserInfoModel.class));
                    }

                    long growth = userInfo.get(0).getGrowth();
                    long weight = userInfo.get(0).getWeight();

                    if (userInfoPreferences.getGrowth() != growth || userInfoPreferences.getWeight() != weight) {
                        userInfoPreferences.writeUserData(growth, weight);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
