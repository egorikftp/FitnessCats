package com.egoriku.catsrunning

class App : DebugApplication() {

    companion object {
        lateinit var appInstance: App
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }
}

