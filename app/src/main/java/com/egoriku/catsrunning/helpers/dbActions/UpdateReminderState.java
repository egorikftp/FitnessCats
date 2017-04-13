package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.helpers.BaseAsyncWriter;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.IS_RINGS;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_REMINDER;

public class UpdateReminderState extends BaseAsyncWriter {
    private int id;
    private int isRing;

    public UpdateReminderState(int id, int isRing) {
        this.id = id;
        this.isRing = isRing;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .updateTable(TABLE_REMINDER)
                .set(IS_RINGS, isRing)
                .updateWhere(_ID_EQ, String.valueOf(id))
                .update();
        return null;
    }
}
