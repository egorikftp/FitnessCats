package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.helpers.BaseAsyncWriter;
import com.egoriku.catsrunning.helpers.InquiryBuilder;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LAT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LNG;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TRACK_ID;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_POINT;

public class WriteLocation extends BaseAsyncWriter {
    private double latitude;
    private double longitude;

    public WriteLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .table(TABLE_POINT)
                .set(LAT, latitude)
                .set(LNG, longitude)
                .set(TRACK_ID, App.getInstance().getFitState().getIdTrack())
                .insert(App.getInstance().getDb());
        return null;
    }
}
