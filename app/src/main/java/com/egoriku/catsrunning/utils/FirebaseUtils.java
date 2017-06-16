package com.egoriku.catsrunning.utils;

import android.content.Context;
import android.widget.Toast;

import com.egoriku.catsrunning.activities.AddUserInfoActivity;
import com.egoriku.catsrunning.models.Firebase.SaveModel;
import com.egoriku.catsrunning.models.Firebase.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

import static com.egoriku.catsrunning.models.Constants.FirebaseFields.TRACKS;
import static com.egoriku.catsrunning.models.Constants.FirebaseFields.USER_INFO;

public class FirebaseUtils {

    private FirebaseUtils() {
    }

    private static FirebaseDatabase firebaseDatabase;
    private static final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static DatabaseReference getDatabaseReference() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }

        return firebaseDatabase.getReference();

    }

    public static void updateTrackFavorire(SaveModel saveModel, final Context context) {
        if (user != null && saveModel.getTrackToken() != null) {
            getDatabaseReference()
                    .child(TRACKS)
                    .child(user.getUid())
                    .child(saveModel.getTrackToken())
                    .setValue(saveModel, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public static void saveUserInfo(UserInfo userInfo, final Context context) {
        if (user != null) {
            getDatabaseReference()
                    .child(USER_INFO)
                    .child(user.getUid())
                    .setValue(userInfo, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public static void removeTrack(SaveModel saveModel, final Context context) {
        if (saveModel.getTrackToken() != null && user != null) {
            getDatabaseReference()
                    .child(TRACKS)
                    .child(user.getUid())
                    .child(saveModel.getTrackToken())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().setValue(null);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static void updateUserInfo(final Context context) {
        Timber.d("ff");
        if (user != null) {
            getDatabaseReference()
                    .child(USER_INFO)
                    .child(user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Timber.d("2");
                            if (!dataSnapshot.exists()) {
                                Timber.d("3");
                                AddUserInfoActivity.start(context);
                            } else {
                                Timber.d("4");
                                UserInfoPreferences userInfoPreferences = new UserInfoPreferences(context);
                                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);

                                int growth = userInfo.getGrowth();
                                int weight = userInfo.getWeight();
                                int age = userInfo.getAge();

                                /*if (userInfoPreferences.getGrowth() != growth || userInfoPreferences.getWeight() != weight) {
                                    userInfoPreferences.writeUserData(growth, weight);
                                }*/
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }
}
