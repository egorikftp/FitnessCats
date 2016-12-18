package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.helpers.BaseAsyncWriter;
import com.egoriku.catsrunning.helpers.InquiryBuilder;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_REMINDER;


public class DeleteReminder extends BaseAsyncWriter {
    private int idReminder;

    public DeleteReminder(int idReminder) {
        this.idReminder = idReminder;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .tableDelete(TABLE_REMINDER)
                .where(false, _ID_EQ, String.valueOf(idReminder))
                .delete();
        return null;
    }
}
