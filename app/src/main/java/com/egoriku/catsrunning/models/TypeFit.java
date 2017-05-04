package com.egoriku.catsrunning.models;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({TypeFit.WALKING, TypeFit.RUNNING, TypeFit.CYCLING})
public @interface TypeFit {
    int WALKING = 1;
    int RUNNING = 2;
    int CYCLING = 3;
}
