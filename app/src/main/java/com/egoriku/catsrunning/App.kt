package com.egoriku.catsrunning

import com.egoriku.core_lib.extensions.DelegatesExt

class App : DebugApplication() {

    companion object {
        @JvmStatic
        var instance: App by DelegatesExt.notNullSingleValue()
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}