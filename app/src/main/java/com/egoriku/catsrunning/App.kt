package com.egoriku.catsrunning

import com.egoriku.catsrunning.kt_util.extensions.DelegatesExt

class App : DebugApplication() {

    companion object {
        var instance: App by DelegatesExt.notNullSingleValue()
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}