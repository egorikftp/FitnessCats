package com.egoriku.catsrunning.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "catsDB.db";
    private static final int DB_VERSION = 1;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (int i = 0; i <= DB_VERSION; i++) {
            migrate(sqLiteDatabase, i);
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        while (oldVersion < newVersion) {
            oldVersion += 1;
            migrate(sqLiteDatabase, oldVersion);
        }
    }


    private void migrate(SQLiteDatabase db, int dbVersion) {
        switch (dbVersion) {
            case 1: {
                Log.e("migrate", "1");
                db.execSQL("CREATE TABLE User (firstName TEXT NOT NULL, lastName TEXT NOT NULL);");
                db.execSQL("CREATE TABLE Tracks (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, idTrackOnServer INTEGER NOT NULL DEFAULT 0, beginsAt INTEGER NOT NULL, time INTEGER NOT NULL DEFAULT 0, distance INTEGER NOT NULL DEFAULT 0, liked INTEGER NOT NULL DEFAULT 0, trackToken TEXT NOT NULL DEFAULT '');");
                db.execSQL("CREATE TABLE Reminder (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dateReminder INTEGER NOT NULL, textReminder TEXT NOT NULL);");
                db.execSQL("CREATE TABLE Point (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, latitude REAL NOT NULL, longitude REAL NOT NULL, trackId INTEGER NOT NULL);");

                db.execSQL("CREATE INDEX user_firstName ON USER (firstName)");
                db.execSQL("CREATE INDEX user_lastName ON USER (lastName)");

                db.execSQL("CREATE INDEX tracks_idTrackOnServer ON Tracks (idTrackOnServer)");
                db.execSQL("CREATE INDEX tracks_beginsAt ON Tracks (beginsAt)");
                db.execSQL("CREATE INDEX tracks_time ON Tracks (time)");
                db.execSQL("CREATE INDEX tracks_distance ON Tracks (distance)");
                db.execSQL("CREATE INDEX tracks_liked ON Tracks (liked)");

                db.execSQL("CREATE INDEX reminder_dateReminder ON Reminder (dateReminder)");
                db.execSQL("CREATE INDEX reminder_textReminder ON Reminder (textReminder)");

                db.execSQL("CREATE INDEX points_latitude ON Point (latitude)");
                db.execSQL("CREATE INDEX points_longitude ON Point (longitude)");
                break;
            }
        }
    }
}
