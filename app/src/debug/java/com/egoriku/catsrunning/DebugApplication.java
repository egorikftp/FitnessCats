package com.egoriku.catsrunning;

import android.annotation.SuppressLint;
import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

@SuppressLint("Registered")
public class DebugApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        LeakCanary.install(this);
    }
}
