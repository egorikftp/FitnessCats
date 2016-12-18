package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.helpers.BaseAsyncWriter;
import com.egoriku.catsrunning.helpers.InquiryBuilder;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class DeleteById extends BaseAsyncWriter {
    private long idTrack;

    public DeleteById(long idTrack) {
        this.idTrack = idTrack;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .tableDelete(TABLE_TRACKS)
                .where(false, _ID_EQ, String.valueOf(idTrack))
                .delete();
        return null;
    }
}
