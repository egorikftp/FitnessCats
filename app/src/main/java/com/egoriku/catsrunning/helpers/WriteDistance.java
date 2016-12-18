package com.egoriku.catsrunning.helpers;

import com.egoriku.catsrunning.App;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.DISTANCE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class WriteDistance extends BaseAsyncWriter {
    private int distance;

    public WriteDistance(int distance) {
        this.distance = distance;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .updateTable(TABLE_TRACKS)
                .set(DISTANCE, distance)
                .updateWhere(_ID_EQ, String.valueOf(App.getInstance().getFitState().getIdTrack()))
                .update();
        return null;
    }
}
