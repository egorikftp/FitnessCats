package com.egoriku.catsrunning;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.egoriku.catsrunning.helpers.DbOpenHelper;
import com.egoriku.catsrunning.models.State;

import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.SQL_VACUUM;


public class App extends Application {
    public static App self;
    private State state;
    private DbOpenHelper dbOpenHelper;
    private SQLiteDatabase db;


    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        dbOpenHelper = new DbOpenHelper(this);
        db = dbOpenHelper.getWritableDatabase();
        db.execSQL(SQL_VACUUM);
    }


    public void createState() {
        state = new State();
    }


    public State getState() {
        return state;
    }


    public SQLiteDatabase getDb() {
        return db;
    }


    public static App getInstance() {
        return self;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

