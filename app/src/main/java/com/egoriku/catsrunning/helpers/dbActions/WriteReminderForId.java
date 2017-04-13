package com.egoriku.catsrunning.helpers.dbActions;

import android.os.AsyncTask;

import com.egoriku.catsrunning.App;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.DATE_REMINDER;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.IS_RINGS;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TYPE_REMINDER;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_RING_TRUE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_REMINDER;

public class WriteReminderForId extends AsyncTask<Void, Integer, Integer> {
    private long dateReminderUnix;
    private int typeReminder;

    public WriteReminderForId(long dateReminderUnix, int typeReminder) {
        this.dateReminderUnix = dateReminderUnix;
        this.typeReminder = typeReminder;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        return (int) new InquiryBuilder()
                .table(TABLE_REMINDER)
                .set(DATE_REMINDER, dateReminderUnix)
                .set(TYPE_REMINDER, typeReminder)
                .set(IS_RINGS, IS_RING_TRUE)
                .insertForId(App.getInstance().getDb());
    }
}
