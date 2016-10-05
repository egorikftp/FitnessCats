package com.egoriku.catsrunning.models;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.egoriku.catsrunning.App;

public class State {
    private static final String KEY_LOGIN = "KEY_LOGIN";
    /**
     * task логина/регистрация
     */
    private boolean isStartTaskAuthentification;

    /**
     * данные сервиса пробежки
     */
    private long sinceTime;
    private int nowDistance;


    public void setLogin(boolean state) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_LOGIN, state);
        editor.apply();
    }

    public boolean isLogin() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        return sharedPref.getBoolean(KEY_LOGIN, false);
    }


    public boolean isStartTaskAuthentification() {
        return isStartTaskAuthentification;
    }

    public void setStartTaskAuthentification(boolean startTaskAuthentification) {
        isStartTaskAuthentification = startTaskAuthentification;
    }

    public long getSinceTime() {
        return sinceTime;
    }

    public void setSinceTime(long sinceTime) {
        this.sinceTime = sinceTime;
    }

    public String getNowDistance() {
        return String.valueOf(nowDistance);
    }

    public void setNowDistance(int nowDistance) {
        this.nowDistance = nowDistance;
    }
}
