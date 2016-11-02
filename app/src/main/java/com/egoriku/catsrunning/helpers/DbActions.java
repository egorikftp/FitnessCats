package com.egoriku.catsrunning.helpers;

import static com.egoriku.catsrunning.models.State.BEGINS_AT_EQ;
import static com.egoriku.catsrunning.models.State.LIKED;
import static com.egoriku.catsrunning.models.State.TABLE_TRACKS;
import static com.egoriku.catsrunning.models.State._ID_EQ;

public class DbActions {
    public static void deleteTrackDataById(int idTrack) {
        new InquiryBuilder()
                .tableDelete(TABLE_TRACKS)
                .where(false, _ID_EQ, String.valueOf(idTrack))
                .delete();

    }


    public static void updateLikedDigit(int likedDigit, int position) {
        new InquiryBuilder()
                .updateTable(TABLE_TRACKS)
                .set(LIKED, likedDigit)
                .updateWhere(_ID_EQ, String.valueOf(position))
                .update();
    }


    public static void deleteSyncTrackData(long beginsAt) {
        new InquiryBuilder()
                .tableDelete(TABLE_TRACKS)
                .where(false, BEGINS_AT_EQ, String.valueOf(beginsAt))
                .delete();
    }
}
