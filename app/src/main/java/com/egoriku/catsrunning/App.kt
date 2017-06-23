package com.egoriku.catsrunning

import com.egoriku.catsrunning.util.extensions.DelegatesExt

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