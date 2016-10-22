package com.egoriku.catsrunning.helpers;

import static com.egoriku.catsrunning.models.State.LIKED;
import static com.egoriku.catsrunning.models.State.TABLE_POINT;
import static com.egoriku.catsrunning.models.State.TABLE_TRACKS;
import static com.egoriku.catsrunning.models.State._ID_EQ;

public class DbActions {
    public static void deleteTrackData(int idTrack) {
        Void deleteTrack = new InquiryBuilder()
                .tableDelete(TABLE_TRACKS)
                .where(false, _ID_EQ, String.valueOf(idTrack))
                .delete();

        Void deletePoints = new InquiryBuilder()
                .tableDelete(TABLE_POINT)
                .where(false, _ID_EQ, String.valueOf(idTrack))
                .delete();
    }


    public static void updateLikedDigit(int likedDigit, int position) {
        Void updateLiked = new InquiryBuilder()
                .updateTable(TABLE_TRACKS)
                .set(LIKED, likedDigit)
                .updateWhere(_ID_EQ, String.valueOf(position))
                .update();
    }
}
