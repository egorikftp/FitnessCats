package com.egoriku.catsrunning;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.egoriku.catsrunning.models.State;

import timber.log.Timber;


public class App extends Application {
    public static App app;
    private State state;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public void createState() {
        state = new State();
    }

    public State getState() {
        return state;
    }

    public static App getInstance() {
        return app;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

