package com.egoriku.catsrunning.utils;

import android.graphics.Typeface;

import com.egoriku.catsrunning.App;

public class CustomFont {
    public static Typeface getTypeFace(){
        return Typeface.createFromAsset(App.appInstance.getAssets(), "font/birusa.ttf");
    }
}
