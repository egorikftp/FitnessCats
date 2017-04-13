package com.egoriku.catsrunning.helpers.dbActions;

import com.egoriku.catsrunning.helpers.BaseAsyncWriter;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LIKED;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class UpdateLikedState extends BaseAsyncWriter {
    private int likedDigit;
    private int position;

    public UpdateLikedState(int likedDigit, int position) {
        this.likedDigit = likedDigit;
        this.position = position;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new InquiryBuilder()
                .updateTable(TABLE_TRACKS)
                .set(LIKED, likedDigit)
                .updateWhere(_ID_EQ, String.valueOf(position))
                .update();
        return null;
    }
}
