package com.egoriku.catsrunning.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.MainActivity;
import com.egoriku.catsrunning.activities.TrackOnMapsActivity;
import com.egoriku.catsrunning.adapters.LikedFragmentAdapter;
import com.egoriku.catsrunning.adapters.interfaces.IRecyclerViewListener;
import com.egoriku.catsrunning.models.LikedTracksModel;

import java.util.ArrayList;

public class LikedFragment extends Fragment {
    public static final String TAG_LIKED_FRAGMENT = "TAG_LIKED_FRAGMENT";
    private static final int UNICODE_SAD_CAT = 0x1F640;
    private static final int LIKED_ID = 1;

    private RecyclerView recyclerViewLikedFragment;
    private TextView noMoreTracksView;

    private ArrayList<LikedTracksModel> likedTracksModels = new ArrayList<>();
    private LikedFragmentAdapter likedFragmentAdapter;

    public LikedFragment() {
    }

    public static LikedFragment newInstance() {
        return new LikedFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_liked, TAG_LIKED_FRAGMENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liked, container, false);
        recyclerViewLikedFragment = (RecyclerView) view.findViewById(R.id.liked_fragment_recycler_view);
        noMoreTracksView = (TextView) view.findViewById(R.id.liked_fragment_no_more_tracks);

        showLikedTracks();
        return view;
    }

    private void showLikedTracks() {
        noMoreTracksView.setText(null);

        Cursor cursor = App.getInstance().getDb().rawQuery("SELECT Tracks._id AS id, Tracks.beginsAt AS date, Tracks.time AS timeRunning, Tracks.distance AS distance, Tracks.liked as liked FROM Tracks WHERE Tracks.liked=?",
                new String[]{String.valueOf(LIKED_ID)}
        );
        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {
                    LikedTracksModel mainModel = new LikedTracksModel();
                    mainModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    mainModel.setDate(cursor.getInt(cursor.getColumnIndexOrThrow("date")));
                    mainModel.setTimeRunning(cursor.getInt(cursor.getColumnIndexOrThrow("timeRunning")));
                    mainModel.setDistance(cursor.getInt(cursor.getColumnIndexOrThrow("distance")));
                    mainModel.setLiked(cursor.getInt(cursor.getColumnIndexOrThrow("liked")));

                    likedTracksModels.add(mainModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (likedTracksModels.size() == 0) {
            noMoreTracksView.setText(getString(R.string.liked_fragment_no_more_liked_tracks) + "" + getEmojiByUnicode(UNICODE_SAD_CAT));
        } else {
            likedFragmentAdapter = new LikedFragmentAdapter(likedTracksModels);
            recyclerViewLikedFragment.setLayoutManager(new LinearLayoutManager(App.getInstance()));
            recyclerViewLikedFragment.setAdapter(likedFragmentAdapter);
            recyclerViewLikedFragment.hasFixedSize();

            likedFragmentAdapter.setOnItemClickListener(new IRecyclerViewListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intentTrackOnMaps = new Intent(getActivity(), TrackOnMapsActivity.class);
                    intentTrackOnMaps.putExtra(TrackOnMapsActivity.KEY_ID, likedTracksModels.get(position).getId());
                    intentTrackOnMaps.putExtra(TrackOnMapsActivity.KEY_DISTANCE, likedTracksModels.get(position).getDistance());
                    intentTrackOnMaps.putExtra(TrackOnMapsActivity.KEY_TIME_RUNNING, likedTracksModels.get(position).getTimeRunning());
                    startActivity(intentTrackOnMaps);
                }

                @Override
                public void onLikedClick(int position) {
                    int likedDigit = 0;
                    updateLikedDigit(likedDigit, likedTracksModels.get(position).getId());
                    likedTracksModels.remove(position);
                    likedFragmentAdapter.notifyItemRemoved(position);
                    likedFragmentAdapter.notifyItemRangeChanged(position, likedTracksModels.size());

                    if (likedTracksModels.size() == 0) {
                        Snackbar.make(recyclerViewLikedFragment, R.string.liked_fragment_snackbar_liked_empty, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }


    private void updateLikedDigit(int likedDigit, int position) {
        SQLiteStatement statement = App.getInstance().getDb().compileStatement(
                "UPDATE Tracks SET liked=? WHERE _id = ?"
        );

        statement.bindLong(1, likedDigit);
        statement.bindLong(2, position);

        try {
            statement.execute();
        } finally {
            statement.close();
        }
    }
}
