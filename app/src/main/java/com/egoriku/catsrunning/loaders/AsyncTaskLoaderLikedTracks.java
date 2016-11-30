package com.egoriku.catsrunning.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.models.AllFitnessDataModel;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.BEGINS_AT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.DISTANCE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LIKED;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TIME;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TRACK_TOKEN;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TYPE_FIT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns._ID;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.AND;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_LIKED;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_TRACK_DELETE_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_TRACK_DELETE_FALSE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.LIKED_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class AsyncTaskLoaderLikedTracks extends AsyncTaskLoader<List<AllFitnessDataModel>> {
    private List<AllFitnessDataModel> likedTracksModels;

    public AsyncTaskLoaderLikedTracks(Context context) {
        super(context);
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (likedTracksModels == null || takeContentChanged()) {
            forceLoad();
        }

        if (likedTracksModels != null) {
            deliverResult(likedTracksModels);
        }
    }


    @Override
    public List<AllFitnessDataModel> loadInBackground() {
        likedTracksModels = new ArrayList<>();
        Cursor cursor = new InquiryBuilder()
                .get(_ID, BEGINS_AT, TIME, DISTANCE, LIKED, TRACK_TOKEN, TYPE_FIT)
                .from(TABLE_TRACKS)
                .where(true, IS_TRACK_DELETE_EQ + " " + IS_TRACK_DELETE_FALSE + " " + AND + " " + LIKED_EQ + " " + String.valueOf(IS_LIKED))
                .orderBy(BEGINS_AT)
                .desc()
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
        return likedTracksModels;
    }


    @Override
    public void deliverResult(List<AllFitnessDataModel> data) {
        likedTracksModels = data;
        super.deliverResult(data);
    }
}
