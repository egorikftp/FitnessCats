package com.egoriku.catsrunning.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.egoriku.catsrunning.models.Firebase.SaveModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.egoriku.catsrunning.models.Constants.FirebaseFields.CHILD_TRACKS;

public class FirebaseUtils {

    private FirebaseUtils() {
    }

    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static void updateTrackFavorire(SaveModel saveModel, final Context context) {
        if (user != null && saveModel.getTrackToken() != null) {
            databaseReference.child(CHILD_TRACKS)
                    .child(user.getUid())
                    .child(saveModel.getTrackToken())
                    .setValue(saveModel, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(context, databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public static void removeTrack(SaveModel saveModel, final Context context) {
        if (saveModel.getTrackToken() != null && user != null) {
            databaseReference
                    .child(CHILD_TRACKS)
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
}
