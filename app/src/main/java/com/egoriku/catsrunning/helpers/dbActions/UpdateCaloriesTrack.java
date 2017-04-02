package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.helpers.BaseAsyncWriter;
import com.egoriku.catsrunning.helpers.InquiryBuilder;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.CALORIES;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class UpdateCaloriesTrack extends BaseAsyncWriter {
    private double calories;

    public UpdateCaloriesTrack(double calories) {
        this.calories = calories;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .updateTable(TABLE_TRACKS)
                .set(CALORIES, calories)
                .updateWhere(_ID_EQ, String.valueOf(App.getInstance().getFitState().getIdTrack()))
                .update();
        return null;
    }
}
