package com.egoriku.catsrunning.loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.models.StatisticModel;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.BEGINS_AT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.DISTANCE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TIME;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.AND;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.AS;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_TRACK_DELETE_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_TRACK_DELETE_FALSE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class AsyncStatisticLoader extends AsyncTaskLoader<List<StatisticModel>> {
    private List<StatisticModel> dataModelList;
    private long timeInterval;

    public AsyncStatisticLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            timeInterval = bundle.getLong("TIME_INTERVAL");
        }
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (dataModelList == null || takeContentChanged()) {
            forceLoad();
        }

        if (dataModelList != null) {
            deliverResult(dataModelList);
        }
    }


    @Override
    public List<StatisticModel> loadInBackground() {
        dataModelList = new ArrayList<>();
        Cursor cursor = new InquiryBuilder()
                .get("SUM(" + DISTANCE + ")" + AS + DISTANCE, BEGINS_AT)
                .from(TABLE_TRACKS)
                .where(true, IS_TRACK_DELETE_EQ + " " + IS_TRACK_DELETE_FALSE + " " + AND + " " + TIME + ">" + 1)
                .sum();

        DbCursor dbCursor = new DbCursor(cursor);
        if (dbCursor.isValid()) {
            do {
                long time = dbCursor.getLong(BEGINS_AT);
                dataModelList.add(new StatisticModel(dbCursor.getInt(DISTANCE)));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return dataModelList;
    }


    @Override
    public void deliverResult(List<StatisticModel> data) {
        dataModelList = data;
        super.deliverResult(data);
    }
}
