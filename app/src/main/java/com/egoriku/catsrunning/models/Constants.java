package com.egoriku.catsrunning.models;

public interface Constants {
    String ANDROID_RESOURCE = "android.resource://";

    interface FormateDate {
        String FORMAT_DATE = "%02d:%02d:%02d";
        String FORMAT_TIME = "HH:mm";
        String FORMAT_DATE_FULL = "dd.MM.yyyy";
        String FORMAT_DATE_LOW = "d MMMM yyyy";
    }

    interface ConstantsSQL {

        interface Tables {
            String TABLE_POINT = "Point";
            String TABLE_TRACKS = "TracksModel";
            String TABLE_REMINDER = "Reminder";
            String TABLE_USER = "User";
        }

        interface Columns {
            String _ID = "_id";
            String LNG = "longitude";
            String LAT = "latitude";
            String TIME = "time";
            String DISTANCE = "distance";
            String LIKED = "liked";
            String TRACK_TOKEN = "trackToken";
            String TYPE_FIT = "typeFit";
            String TRACK_ID = "trackId";
            String CALORIES = "calories";
            String DATE_REMINDER = "dateReminder";
            String TYPE_REMINDER = "typeReminder";
            String IS_RINGS = "isRings";
            String IS_TRACK_DELETE = "isTrackDelete";
        }

        interface Query {
            String DISTANCE_COUNT = "distance_count";
            String BEGINS_AT_EQ = "beginsAt=";
            String TRACK_ID_EQ = "trackId=";
            String TYPE_FIT_EQ = "typeFit=";
            String IS_TRACK_DELETE_EQ = "isTrackDelete=";
            String _ID_EQ = "_id=";
            String LIKED_EQ = "liked=";

            String IS_TRACK_DELETE_TRUE = "1";
            String IS_TRACK_DELETE_FALSE = "0";
            String IS_LIKED = "1";

            int IS_RING_TRUE = 1;
            int IS_RING_FALSE = 0;

            String LEFT_BRACKET = "(";
            String RIGHT_BRACKET = ")";
            String MORE_THEN = ">";

            String SELECT = "SELECT";
            String SELECT_FROM = "SELECT FROM";
            String DELETE = "DELETE FROM";
            String WHERE = "WHERE";
            String AS = " AS ";
            String SUM = "SUM";
            String FROM = "FROM";
            String JOIN = "JOIN";
            String ON = "ON";
            String AND = " AND ";
            String CREATE = "CREATE TABLE";
            String CREATE_INDEX = "CREATE INDEX";
            String DROP_INDEX = "DROP INDEX";
            String DROP = "DROP TABLE";
            String ALTER = "ALTER TABLE";
            String RENAME = "RENAME TO";
            String INSERT = "INSERT INTO";
            String UPDATE = "UPDATE";
            String SET = "SET";
            String ORDER_BY = "ORDER BY";
            String DESC = "DESC";
            String GROUP_BY = "GROUP BY";
            String VALUES = "VALUES";
            String PRIMARY_KEY = "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT";
            String EQ_QUESTION = "=?";
        }
    }

    interface Extras {
        String EXTRA_ID_REMINDER_KEY = "EXTRA_ID_REMINDER_KEY";
        String EXTRA_TEXT_TYPE_REMINDER_KEY = "EXTRA_TEXT_TYPE_REMINDER_KEY";
        String TYPE_REMINDER_KEY = "TYPE_REMINDER_KEY";
        String KEY_TYPE_FIT = "KEY_TYPE_FIT";
        String EXTRA_TRACK_ON_MAPS = "EXTRA_TRACK_ON_MAPS";
    }

    interface Tags {
        String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";
    }

    interface Broadcast {
        String BROADCAST_ADD_NEW_REMINDER = "BROADCAST_ADD_NEW_REMINDER";
        String BROADCAST_UPDATE_REMINDER_DATE = "BROADCAST_UPDATE_REMINDER_DATE";
        String BROADCAST_UPDATE_REMINDER_TIME = "BROADCAST_UPDATE_REMINDER_TIME";
    }

    interface KeyReminder {
        String KEY_ID = "KEY_ID";
        String KEY_TYPE_REMINDER = "KEY_TYPE_REMINDER";
        String KEY_DATE_REMINDER = "KEY_DATE_REMINDER";
    }

    interface KeyNotification {
        String KEY_TYPE_FIT_NOTIFICATION = "KEY_TYPE_FIT_NOTIFICATION";
    }

    interface ModelReminder {
        String KEY_VISIBLE_STRING_PICKER = "KEY_VISIBLE_STRING_PICKER";
        String KEY_VISIBLE_DATE_PICKER = "KEY_VISIBLE_DATE_PICKER";
        String KEY_VISIBLE_TIME_PICKER = "KEY_VISIBLE_TIME_PICKER";
        String KEY_VISIBLE_BTN_BACK = "KEY_VISIBLE_BTN_BACK";
        String KEY_CONDITION = "KEY_CONDITION";
        String KEY_CALENDAR = "KEY_CALENDAR";
        String KEY_TOOLBAR = "KEY_TOOLBAR";
        String KEY_VISIBLE_TEXT_VIEW = "KEY_VISIBLE_TEXT_VIEW";
    }

    interface ModelRegister {
        String PARCELABLE_REGISTER = "PARCELABLE_REGISTER";
    }

    interface ModelScamperActivity {
        String PARCELABLE_FIT_ACTIVITY_KEY = "PARCELABLE_FIT_ACTIVITY_KEY";
        String KEY_IS_CHRONOMETER_RUNNING = "KEY_IS_CHRONOMETER_RUNNING";
        String KEY_START_TIME = "KEY_START_TIME";
    }

    interface CustomStringPicker {
        String SET_CURRENT = "setCurrent";
        String SET_VALUE = "setValue";
        String GET_CURRENT = "getCurrent";
        String GET_VALUE = "getValue";
        String SET_RANGE = "setRange";
        String SET_MAX_VALUE = "setMaxValue";
        String SET_MIN_VALUE = "setMinValue";
        String SET_DISPLAYED_VALUES = "setDisplayedValues";
        String SET_DESCENDANT_FOCUSABILITY = "setDescendantFocusability";
        String COM_ANDROID_INTERNAL_WIDGET_NUMBER_PICKER = "com.android.internal.widget.NumberPicker";
        String ANDROID_WIDGET_NUMBER_PICKER = "android.widget.NumberPicker";
    }

    interface FitChart {
        String ANIMATION_SEEK = "animationSeek";
    }

    interface RunService {
        String START_TIME = "START_TIME";
        String ACTION_START = "START_NOTIFY_SERVICE";
    }

    interface TracksOnMapActivity {
        String KEY_ID = "KEY_ID";
        String KEY_DISTANCE = "KEY_DISTANCE";
        String KEY_TIME_RUNNING = "KEY_TIME_RUNNING";
        String KEY_LIKED = "KEY_LIKED";
        String KEY_TOKEN = "KEY_TOKEN";
        String KEY_TYPE_FIT = "KEY_TYPE_FIT";
    }

    interface Color{
        String COLOR_NOW_FIT = "#CFDCE6";
    }

    interface UserInfoSharedPreference{
        String KEY_WEIGHT = "KEY_WEIGHT";
        String KEY_GROWTH = "KEY_GROWTH";
        int DEFAULT_WEIGHT = 50;
        int DEFAULT_GROWTH = 150;
    }

    interface FirebaseFields{
        String TYPE_FIT = "typeFit";
        String IS_FAVORIRE = "favorite";
        String BEGINS_AT = "beginsAt";

        String TRACKS = "tracks";
        String USER_INFO = "user_info";
    }
}
