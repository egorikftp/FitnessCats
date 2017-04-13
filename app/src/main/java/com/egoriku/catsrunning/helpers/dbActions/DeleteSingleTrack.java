package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.helpers.BaseAsyncWriter;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.BEGINS_AT_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class DeleteSingleTrack extends BaseAsyncWriter {
    private long beginsAt;

    public DeleteSingleTrack(long beginsAt) {
        this.beginsAt = beginsAt;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .tableDelete(TABLE_TRACKS)
                .where(false, BEGINS_AT_EQ, String.valueOf(beginsAt))
                .delete();
        return null;
    }
}
