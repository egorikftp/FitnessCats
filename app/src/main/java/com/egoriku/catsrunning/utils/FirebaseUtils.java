package com.egoriku.catsrunning.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.AddUserInfoActivity;
import com.egoriku.catsrunning.data.commons.TracksModel;
import com.egoriku.catsrunning.models.Firebase.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.egoriku.catsrunning.models.Constants.FirebaseFields.TRACKS;
import static com.egoriku.catsrunning.models.Constants.FirebaseFields.USER_INFO;

public class FirebaseUtils {

    private FirebaseUtils() {
    }

    private static FirebaseDatabase firebaseDatabase;
    private static FirebaseUser user;

    private static FirebaseUtils firebaseUtils = null;

    public static FirebaseUtils getInstance() {
        if (firebaseUtils == null) {
            firebaseUtils = new FirebaseUtils();
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUtils;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public DatabaseReference getFirebaseDatabase() {
        return firebaseDatabase.getReference();
    }

    public void updateFavorite(final TracksModel tracksModel, final Context context) {
        if (getUser() != null && tracksModel.getTrackToken() != null) {
            getFirebaseDatabase()
                    .child(TRACKS)
                    .child(getUser().getUid())
                    .child(tracksModel.getTrackToken())
                    .setValue(tracksModel, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void saveUserInfo(UserInfo userInfo, final Context context) {
        if (getUser() != null) {
            getFirebaseDatabase()
                    .child(USER_INFO)
                    .child(getUser().getUid())
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

    public String getTrackToken() {
        String trackToken = null;

        if (getUser() != null) {
            trackToken = getFirebaseDatabase()
                    .child(TRACKS)
                    .child(user.getUid())
                    .push()
                    .getKey();
        }

        return trackToken;
    }

    public void saveFit(@NonNull TracksModel tracksModel, @NonNull final View view) {
        if (getUser() != null && tracksModel.getTrackToken() != null) {
            getFirebaseDatabase()
                    .child(TRACKS)
                    .child(user.getUid())
                    .child(tracksModel.getTrackToken())
                    .setValue(tracksModel, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Snackbar.make(view, R.string.scamper_activity_track_save_error + " " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(view, R.string.scamper_activity_track_save_success, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void removeTrack(TracksModel tracksModel, final Context context) {
        if (tracksModel.getTrackToken() != null && getUser() != null) {
            getFirebaseDatabase()
                    .child(TRACKS)
                    .child(getUser().getUid())
                    .child(tracksModel.getTrackToken())
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

    public void updateUserInfo(final Context context) {
        if (getUser() != null) {
            getFirebaseDatabase()
                    .child(USER_INFO)
                    .child(getUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                AddUserInfoActivity.start(context);
                            } else {
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
