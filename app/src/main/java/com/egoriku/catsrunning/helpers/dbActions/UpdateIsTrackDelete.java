package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.helpers.BaseAsyncWriter;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.IS_TRACK_DELETE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_TRACK_DELETE_TRUE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;


public class UpdateIsTrackDelete extends BaseAsyncWriter {
    private int id;

    public UpdateIsTrackDelete(int id) {
        this.id = id;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .updateTable(TABLE_TRACKS)
                .set(IS_TRACK_DELETE, IS_TRACK_DELETE_TRUE)
                .updateWhere(_ID_EQ, String.valueOf(id))
                .update();
        return null;
    }
}
