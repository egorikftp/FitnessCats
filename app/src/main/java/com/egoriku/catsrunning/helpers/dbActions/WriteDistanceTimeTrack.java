package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.helpers.BaseAsyncWriter;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.DISTANCE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TIME;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class WriteDistanceTimeTrack extends BaseAsyncWriter {
    private float nowDistance;
    private long sinceTime;

    public WriteDistanceTimeTrack(float nowDistance, long sinceTime) {
        this.nowDistance = nowDistance;
        this.sinceTime = sinceTime;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .updateTable(TABLE_TRACKS)
                .set(DISTANCE, nowDistance)
                .set(TIME, sinceTime)
                .updateWhere(_ID_EQ, String.valueOf(App.getInstance().getFitState().getIdTrack()))
                .update();
        return null;
    }
}
