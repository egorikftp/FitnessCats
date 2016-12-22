package com.egoriku.catsrunning.helpers.dbActions;

import android.database.Cursor;
import android.os.AsyncTask;

import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;

import java.util.HashSet;
import java.util.Set;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.BEGINS_AT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class GetListBeeginsAt extends AsyncTask<Void, Set<Long>, Set<Long>> {
    private Set<Long> localDbDate;

    public GetListBeeginsAt() {
        this.localDbDate = new HashSet<>();
    }

    @Override
    protected Set<Long> doInBackground(Void... voids) {
        Cursor cursor = new InquiryBuilder()
                .get(BEGINS_AT)
                .from(TABLE_TRACKS)
                .select();

        DbCursor dbCursor = new DbCursor(cursor);
        if (dbCursor.isValid()) {
            do {
                localDbDate.add(dbCursor.getLong(BEGINS_AT));
            } while (cursor.moveToNext());
        }
        dbCursor.close();
        return localDbDate;
    }
}
