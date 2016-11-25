package com.egoriku.catsrunning.fragments;

import android.content.Intent;
import android.database.Cursor;
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
import com.egoriku.catsrunning.activities.TrackOnMapsActivity;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.egoriku.catsrunning.adapters.LikedFragmentAdapter;
import com.egoriku.catsrunning.adapters.interfaces.ILikedClickListener;
import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.models.AllFitnessDataModel;

import java.util.ArrayList;

import static com.egoriku.catsrunning.helpers.DbActions.updateLikedDigit;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.BEGINS_AT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.DISTANCE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LIKED;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TIME;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TRACK_TOKEN;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TYPE_FIT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns._ID;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_LIKED;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.LIKED_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;
import static com.egoriku.catsrunning.models.Constants.Tags.TAG_LIKED_FRAGMENT;
import static com.egoriku.catsrunning.models.Constants.TracksOnMApActivity.KEY_DISTANCE;
import static com.egoriku.catsrunning.models.Constants.TracksOnMApActivity.KEY_ID;
import static com.egoriku.catsrunning.models.Constants.TracksOnMApActivity.KEY_LIKED;
import static com.egoriku.catsrunning.models.Constants.TracksOnMApActivity.KEY_TIME_RUNNING;
import static com.egoriku.catsrunning.models.Constants.TracksOnMApActivity.KEY_TOKEN;
import static com.egoriku.catsrunning.models.Constants.TracksOnMApActivity.KEY_TYPE_FIT;

public class LikedFragment extends Fragment {
    private static final int UNICODE_SAD_CAT = 0x1F640;
    private RecyclerView recyclerView;
    private TextView noMoreTracksView;
    private ArrayList<AllFitnessDataModel> likedTracksModels;
    private LikedFragmentAdapter likedFragmentAdapter;


    public LikedFragment() {
    }


    public static LikedFragment newInstance() {
        return new LikedFragment();
    }


    @Override
    public void onStart() {
        super.onStart();
        ((TracksActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_liked, TAG_LIKED_FRAGMENT);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liked, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.liked_fragment_recycler_view);
        noMoreTracksView = (TextView) view.findViewById(R.id.liked_fragment_no_more_tracks);

        likedTracksModels = new ArrayList<>();
        likedFragmentAdapter = new LikedFragmentAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getInstance()));
        recyclerView.hasFixedSize();

        showLikedTracks();
        return view;
    }


    private void showLikedTracks() {
        noMoreTracksView.setText(null);

        Cursor cursor = new InquiryBuilder()
                .get(_ID, BEGINS_AT, TIME, DISTANCE, LIKED, TRACK_TOKEN, TYPE_FIT)
                .from(TABLE_TRACKS)
                .where(false, LIKED_EQ, String.valueOf(IS_LIKED))
                .select();

        DbCursor dbCursor = new DbCursor(cursor);
        if (dbCursor.isValid()) {
            do {
                AllFitnessDataModel likedItem = new AllFitnessDataModel();
                likedItem.setId(dbCursor.getInt(_ID));
                likedItem.setBeginsAt(dbCursor.getInt(BEGINS_AT));
                likedItem.setTime(dbCursor.getInt(TIME));
                likedItem.setDistance(dbCursor.getInt(DISTANCE));
                likedItem.setLiked(dbCursor.getInt(LIKED));
                likedItem.setTrackToken(dbCursor.getString(TRACK_TOKEN));
                likedItem.setTypeFit(dbCursor.getInt(TYPE_FIT));
                likedTracksModels.add(likedItem);
            } while (cursor.moveToNext());
        }
        dbCursor.close();

        if (likedTracksModels.size() == 0) {
            noMoreTracksView.setText(getString(R.string.liked_fragment_no_more_liked_tracks) + "" + getEmojiByUnicode(UNICODE_SAD_CAT));
        } else {
            likedFragmentAdapter.setData(likedTracksModels);
            recyclerView.setAdapter(likedFragmentAdapter);

            likedFragmentAdapter.setOnItemClickListener(new ILikedClickListener() {
                @Override
                public void onItemClick(AllFitnessDataModel item, int position) {
                    Intent intentTrackOnMaps = new Intent(getActivity(), TrackOnMapsActivity.class);
                    intentTrackOnMaps.putExtra(KEY_ID, item.getId());
                    intentTrackOnMaps.putExtra(KEY_DISTANCE, item.getDistance());
                    intentTrackOnMaps.putExtra(KEY_TIME_RUNNING, item.getTime());
                    intentTrackOnMaps.putExtra(KEY_TYPE_FIT, item.getTypeFit());
                    intentTrackOnMaps.putExtra(KEY_LIKED, item.getLiked());
                    intentTrackOnMaps.putExtra(KEY_TOKEN, item.getTrackToken());
                    startActivity(intentTrackOnMaps);
                }

                @Override
                public void onLikedClick(AllFitnessDataModel item, int position) {
                    int likedDigit = 0;
                    updateLikedDigit(likedDigit, likedTracksModels.get(position).getId());
                    likedTracksModels.remove(position);
                    likedFragmentAdapter.notifyItemRemoved(position);
                    likedFragmentAdapter.notifyItemRangeChanged(position, likedTracksModels.size());

                    if (likedTracksModels.size() == 0) {
                        Snackbar.make(recyclerView, R.string.liked_fragment_snackbar_liked_empty, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}
