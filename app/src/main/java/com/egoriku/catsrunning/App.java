package com.egoriku.catsrunning;

public class App extends DebugApplication {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static App getInstance() {
        return app;
    }

}

