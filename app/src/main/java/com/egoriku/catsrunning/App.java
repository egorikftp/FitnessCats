package com.egoriku.catsrunning;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.egoriku.catsrunning.helpers.DbOpenHelper;
import com.egoriku.catsrunning.models.State;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class App extends Application {
    private static String CHILD_TRACKS = "tracks";

    public static App self;
    private State state;
    private DbOpenHelper dbOpenHelper;
    private SQLiteDatabase db;

    private DatabaseReference database;
    private DatabaseReference tracksReference;


    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        dbOpenHelper = new DbOpenHelper(this);
        db = dbOpenHelper.getWritableDatabase();
        db.execSQL("VACUUM");
        database = FirebaseDatabase.getInstance().getReference();
        tracksReference = database.child(CHILD_TRACKS);
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

    public DatabaseReference getTracksReference() {
        return tracksReference;
    }
}
