package com.egoriku.catsrunning.loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.egoriku.catsrunning.models.StatisticModel;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.BEGINS_AT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.DISTANCE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.AND;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.AS;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.DISTANCE_COUNT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_TRACK_DELETE_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_TRACK_DELETE_FALSE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.LEFT_BRACKET;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.MORE_THEN;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.RIGHT_BRACKET;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.SUM;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.TYPE_FIT_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;
import static com.egoriku.catsrunning.models.Constants.Extras.KEY_BUNDLE_TIME_AMOUNT;

public class AsyncStatisticLoader extends AsyncTaskLoader<List<StatisticModel>> {
    private List<StatisticModel> dataModelList;
    private long timeInterval;

    public AsyncStatisticLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            timeInterval = bundle.getLong(KEY_BUNDLE_TIME_AMOUNT);
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
        for (int typeFit = 0; typeFit <= 2; typeFit++) {
            getFitStatisticDb(typeFit);
        }

        return dataModelList;
    }


    private void getFitStatisticDb(int typeFit) {
        int queryFit = typeFit + 1;
        Cursor cursor = new InquiryBuilder()
                .get(SUM + LEFT_BRACKET + DISTANCE + RIGHT_BRACKET + AS + DISTANCE_COUNT)
                .from(TABLE_TRACKS)
                .where(true, IS_TRACK_DELETE_EQ + " " + IS_TRACK_DELETE_FALSE + AND + BEGINS_AT + MORE_THEN + timeInterval + AND + TYPE_FIT_EQ + queryFit)
                .sum();

        DbCursor dbCursor = new DbCursor(cursor);
        if (dbCursor.isValid()) {
            do {
                dataModelList.add(typeFit, new StatisticModel(dbCursor.getInt(DISTANCE_COUNT)));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }


    @Override
    public void deliverResult(List<StatisticModel> data) {
        dataModelList = data;
        super.deliverResult(data);
    }
}
