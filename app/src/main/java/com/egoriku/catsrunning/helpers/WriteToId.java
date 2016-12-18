package com.egoriku.catsrunning.helpers;

import com.egoriku.catsrunning.App;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.BEGINS_AT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TYPE_FIT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class WriteToId extends BaseAsyncWriter {
    private long id;
    private int typeFit;

    public WriteToId(int typeFit) {
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
