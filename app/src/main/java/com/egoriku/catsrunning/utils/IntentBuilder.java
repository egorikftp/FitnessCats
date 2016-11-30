package com.egoriku.catsrunning.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class IntentBuilder {
    private Intent intent;
    private Context context;


    public IntentBuilder() {
        intent = new Intent();
    }


    public IntentBuilder context(Context context) {
        this.context = context;
        return this;
    }


    public Intent build() {
        return intent;
    }


    /**
     * Builder Methods (action, activity, flags)
     */

    public IntentBuilder action(String action) {
        intent.setAction(action);
        return this;
    }


    public IntentBuilder activity(Class<? extends Activity> activity) {
        intent.setClass(context, activity);
        return this;
    }


    public IntentBuilder service(Class<? extends Service> service) {
        intent.setClass(context, service);
        return this;
    }


    public IntentBuilder flags(int... flags) {
        for (int flag : flags) {
            intent.addFlags(flag);
        }
        return this;
    }


    /**
     * Builder Extras
     */

    public IntentBuilder extra(String name, boolean value) {
        intent.putExtra(name, value);
        return this;
    }

    public IntentBuilder extra(String name, char value) {
        intent.putExtra(name, value);
        return this;
    }

    public IntentBuilder extra(String name, double value) {
        intent.putExtra(name, value);
        return this;
    }

    public IntentBuilder extra(String name, float value) {
        intent.putExtra(name, value);
        return this;
    }

    public IntentBuilder extra(String name, int value) {
        intent.putExtra(name, value);
        return this;
    }

    public IntentBuilder extra(String name, long value) {
        intent.putExtra(name, value);
        return this;
    }

    public IntentBuilder extra(String name, short value) {
        intent.putExtra(name, value);
        return this;
    }

    public IntentBuilder extra(String name, String value) {
        intent.putExtra(name, value);
        return this;
    }

    public IntentBuilder extra(String name, Bundle value) {
        intent.putExtra(name, value);
        return this;
    }
}
