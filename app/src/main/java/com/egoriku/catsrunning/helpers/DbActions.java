package com.egoriku.catsrunning.helpers;

import com.egoriku.catsrunning.helpers.dbActions.DeleteById;
import com.egoriku.catsrunning.helpers.dbActions.DeleteReminder;
import com.egoriku.catsrunning.helpers.dbActions.DeleteSingleTrack;
import com.egoriku.catsrunning.helpers.dbActions.UpdateIsTrackDelete;
import com.egoriku.catsrunning.helpers.dbActions.UpdateLikedState;
import com.egoriku.catsrunning.helpers.dbActions.UpdateReminder;
import com.egoriku.catsrunning.helpers.dbActions.UpdateReminderState;
import com.egoriku.catsrunning.helpers.dbActions.WriteDistanceTimeTrack;
import com.egoriku.catsrunning.helpers.dbActions.WriteDistanceTrack;
import com.egoriku.catsrunning.helpers.dbActions.WriteForResultId;
import com.egoriku.catsrunning.helpers.dbActions.WriteLocation;
import com.egoriku.catsrunning.helpers.dbActions.WriteReminderForId;
import com.egoriku.catsrunning.helpers.dbActions.WriteToken;

import java.util.concurrent.ExecutionException;

public class DbActions {

    public static void insertLocationDb(double longitude, double latitude) {
        new WriteLocation(latitude, longitude).execute();
    }

    public static void insertDistanceTime(float nowDistance, long sinceTime) {
        new WriteDistanceTimeTrack(nowDistance, sinceTime).execute();
    }


    public static void insertToId(int typeFit) {
        new WriteForResultId(typeFit).execute();
    }


    public static void deleteTrackDataById(long idTrack) {
        new DeleteById(idTrack).execute();
    }


    public static void writeTrackToken(String key, long idTrack) {
        new WriteToken(key, idTrack).execute();
    }


    public static void writeDistance(int distance) {
        new WriteDistanceTrack(distance).execute();
    }


    public static void updateLikedDigit(int likedDigit, int position) {
        new UpdateLikedState(likedDigit, position).execute();
    }


    public static void deleteSyncTrackData(long beginsAt) {
        new DeleteSingleTrack(beginsAt).execute();
    }


    public static int writeReminderDb(long dateReminderUnix, int typeReminder) throws ExecutionException, InterruptedException {
        return new WriteReminderForId(dateReminderUnix, typeReminder).execute().get();
    }


    public static void deleteReminderDb(int idReminder) {
        new DeleteReminder(idReminder).execute();
    }


    public static void updateReminder(long dateReminderUnix, int id) {
        new UpdateReminder(dateReminderUnix, id).execute();
    }


    public static void updateAlarmCondition(int id, int isRing) {
        new UpdateReminderState(id, isRing).execute();
    }


    public static void updateIsTrackDelete(int id) {
        new UpdateIsTrackDelete(id).execute();
    }
}
