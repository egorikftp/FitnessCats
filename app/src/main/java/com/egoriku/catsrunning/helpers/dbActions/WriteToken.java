package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.helpers.BaseAsyncWriter;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TRACK_TOKEN;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class WriteToken extends BaseAsyncWriter {
    private String key;
    private long idTrack;

    public WriteToken(String key, long idTrack) {
        this.key = key;
        this.idTrack = idTrack;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .updateTable(TABLE_TRACKS)
                .set(TRACK_TOKEN, key)
                .updateWhere(_ID_EQ, String.valueOf(idTrack))
                .update();
        return null;
    }
}
