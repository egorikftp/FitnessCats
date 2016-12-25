package com.egoriku.catsrunning.helpers;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.helpers.dbActions.AsyncWriteNewTracks;
import com.egoriku.catsrunning.models.AllFitnessDataModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.egoriku.catsrunning.helpers.DbActions.deleteSyncTrackData;
import static com.egoriku.catsrunning.helpers.DbActions.getLocaleTracksBeginsAt;
import static com.egoriku.catsrunning.models.Constants.Broadcast.BROADCAST_SAVE_NEW_TRACKS;
import static com.egoriku.catsrunning.models.Constants.ConstantsFirebase.CHILD_TRACKS;

public class FirebaseSync {
    private static FirebaseSync firebaseSync;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private long countTracks;
    private List<Long> localeDbDate;

    public static FirebaseSync getInstance() {
        if (firebaseSync == null) {
            firebaseSync = new FirebaseSync();
        }
        return firebaseSync;
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {
            new Thread() {
                @Override
                public void run() {
                    List<AllFitnessDataModel> allFitnessDataModels = new ArrayList<>();
                    countTracks = dataSnapshot.getChildrenCount();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        allFitnessDataModels.add(snapshot.getValue(AllFitnessDataModel.class));
                    }

                    try {
                        localeDbDate = new ArrayList<>(getLocaleTracksBeginsAt());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < allFitnessDataModels.size(); i++) {
                        if (!localeDbDate.contains(allFitnessDataModels.get(i).getBeginsAt())) {
                            AsyncWriteNewTracks.writeData(allFitnessDataModels.get(i), countTracks);
                        }
                        countTracks--;
                    }
                }
            }.start();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            deleteSyncTrackData(dataSnapshot.getValue(AllFitnessDataModel.class).getBeginsAt());
            LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcastSync(new Intent(BROADCAST_SAVE_NEW_TRACKS));
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    public void startSync(FirebaseUser user) {
        firebaseDatabase.getReference().child(CHILD_TRACKS).child(user.getUid()).addValueEventListener(valueEventListener);
        firebaseDatabase.getReference().child(CHILD_TRACKS).child(user.getUid()).addChildEventListener(childEventListener);
    }
}
