package com.egoriku.catsrunning

import android.annotation.SuppressLint
import android.support.multidex.MultiDexApplication
import timber.log.Timber

@SuppressLint("Registered")
open class DebugApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        DebugInitializer.register(this)
        LeakCanaryInitializer.register(this, false)
    }
}
