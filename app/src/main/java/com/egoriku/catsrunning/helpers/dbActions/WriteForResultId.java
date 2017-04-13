package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.helpers.BaseAsyncWriter;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.BEGINS_AT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TYPE_FIT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class WriteForResultId extends BaseAsyncWriter {
    private long id;
    private int typeFit;

    public WriteForResultId(int typeFit) {
        this.typeFit = typeFit;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        id = new InquiryBuilder()
                .table(TABLE_TRACKS)
                .set(BEGINS_AT, App.getInstance().getFitState().getStartTime() / 1000)
                .set(TYPE_FIT, typeFit)
                .insertForId(App.getInstance().getDb());
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        App.getInstance().getFitState().setIdTrack(id);
    }
}
