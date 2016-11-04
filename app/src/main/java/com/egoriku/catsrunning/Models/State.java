package com.egoriku.catsrunning.models;

public class State {
    public static final String EXTRA_ID_REMINDER_KEY = "EXTRA_ID_REMINDER_KEY";
    public static final String EXTRA_TEXT_TYPE_REMINDER_KEY = "EXTRA_TEXT_TYPE_REMINDER_KEY";
    public static final String TYPE_REMINDER_KEY = "TYPE_REMINDER_KEY";

    public static final String KEY_TYPE_FIT = "KEY_TYPE_FIT";

    public static final String TABLE_POINT = "Point";
    public static final String TABLE_TRACKS = "Tracks";
    public static final String TABLE_REMINDER = "Reminder";

    public static final String AND = "AND";

    public static final String _ID = "_id";
    public static final String LNG = "longitude";
    public static final String LAT = "latitude";
    public static final String BEGINS_AT = "beginsAt";
    public static final String TIME = "time";
    public static final String DISTANCE = "distance";
    public static final String LIKED = "liked";
    public static final String TRACK_TOKEN = "trackToken";
    public static final String TYPE_FIT = "typeFit";
    public static final String TRACK_ID= "trackId";
    public static final String DATE_REMINDER= "dateReminder";
    public static final String TYPE_REMINDER = "typeReminder";



    public static final String BEGINS_AT_EQ = "beginsAt=";
    public static final String TRACK_ID_EQ = "trackId=";
    public static final String IS_TRACK_DELETE_EQ = "isTrackDelete=";
    public static final String _ID_EQ = "_id=";

    public static final String IS_TRACK_DELETE_TRUE = "1";
    public static final String  IS_TRACK_DELETE_FALSE = "0";

    public static final int TYPE_FIT_WALK = 1;
    public static final int TYPE_FIT_RUN = 2;
    public static final int TYPE_FIT_CYCLING = 3;

    private static final String KEY_LOGIN = "KEY_LOGIN";
    /**
     * task логина/регистрация
     */
    private boolean isStartTaskAuthentification;

    /**
     * данные сервиса пробежки
     */
    private long sinceTime;
    private int nowDistance;

    public boolean isStartTaskAuthentification() {
        return isStartTaskAuthentification;
    }

    public void setStartTaskAuthentification(boolean startTaskAuthentification) {
        isStartTaskAuthentification = startTaskAuthentification;
    }

    public long getSinceTime() {
        return sinceTime;
    }

    public void setSinceTime(long sinceTime) {
        this.sinceTime = sinceTime;
    }

    public String getNowDistance() {
        return String.valueOf(nowDistance);
    }

    public void setNowDistance(int nowDistance) {
        this.nowDistance = nowDistance;
    }
}
