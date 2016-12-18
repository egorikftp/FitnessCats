package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.helpers.BaseAsyncWriter;
import com.egoriku.catsrunning.helpers.InquiryBuilder;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.DATE_REMINDER;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.IS_RINGS;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_RING_TRUE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_REMINDER;


public class UpdateReminder extends BaseAsyncWriter {
    private long dateReminderUnix;
    private int id;

    public UpdateReminder(long dateReminderUnix, int id) {
        this.dateReminderUnix = dateReminderUnix;
        this.id = id;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .updateTable(TABLE_REMINDER)
                .set(DATE_REMINDER, dateReminderUnix)
                .set(IS_RINGS, IS_RING_TRUE)
                .updateWhere(_ID_EQ, String.valueOf(id))
                .update();
        return null;
    }
}
