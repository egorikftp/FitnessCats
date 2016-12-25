package com.egoriku.catsrunning.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.egoriku.catsrunning.models.Constants.UserInfoSharedPreference.DEFAULT_GROWTH;
import static com.egoriku.catsrunning.models.Constants.UserInfoSharedPreference.DEFAULT_WEIGHT;
import static com.egoriku.catsrunning.models.Constants.UserInfoSharedPreference.KEY_GROWTH;
import static com.egoriku.catsrunning.models.Constants.UserInfoSharedPreference.KEY_WEIGHT;

public class UserInfoPreferences {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public UserInfoPreferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void writeUserData(long growth, long weight){
        editor.putLong(KEY_GROWTH, growth);
        editor.putLong(KEY_WEIGHT, weight);
        editor.apply();
    }

    public long getWeight(){
        return sharedPreferences.getLong(KEY_WEIGHT, DEFAULT_WEIGHT);
    }

    public long getGrowth(){
        return sharedPreferences.getLong(KEY_GROWTH, DEFAULT_GROWTH);
    }
}
