package com.egoriku.catsrunning.helpers;

import com.egoriku.catsrunning.App;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.DATE_REMINDER;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.IS_RINGS;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.IS_TRACK_DELETE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LIKED;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TYPE_REMINDER;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.BEGINS_AT_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_RING_TRUE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.IS_TRACK_DELETE_TRUE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_REMINDER;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

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


    public static int writeReminderDb(long dateReminderUnix, int typeReminder) {
        return (int)  new InquiryBuilder()
                .table(TABLE_REMINDER)
                .set(DATE_REMINDER, dateReminderUnix)
                .set(TYPE_REMINDER, typeReminder)
                .set(IS_RINGS, IS_RING_TRUE)
                .insertForId(App.getInstance().getDb());
    }


    public static void deleteReminderDb(int idReminder) {
        new InquiryBuilder()
                .tableDelete(TABLE_REMINDER)
                .where(false, _ID_EQ, String.valueOf(idReminder))
                .delete();
    }


    public static void updateReminder(long dateReminderUnix, int id) {
        new InquiryBuilder()
                .updateTable(TABLE_REMINDER)
                .set(DATE_REMINDER, dateReminderUnix)
                .set(IS_RINGS, IS_RING_TRUE)
                .updateWhere(_ID_EQ, String.valueOf(id))
                .update();
    }


    public static void updateAlarmCondition(int id, int isRing) {
        new InquiryBuilder()
                .updateTable(TABLE_REMINDER)
                .set(IS_RINGS, isRing)
                .updateWhere(_ID_EQ, String.valueOf(id))
                .update();
    }


    public static void updateIsTrackDelete(int id) {
        new InquiryBuilder()
                .updateTable(TABLE_TRACKS)
                .set(IS_TRACK_DELETE, IS_TRACK_DELETE_TRUE)
                .updateWhere(_ID_EQ, String.valueOf(id))
                .update();
    }

}
