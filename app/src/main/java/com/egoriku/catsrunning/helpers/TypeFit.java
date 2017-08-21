package com.egoriku.catsrunning.helpers;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({TypeFit.UNCERTAIN, TypeFit.WALKING, TypeFit.RUNNING, TypeFit.CYCLING})
public @interface TypeFit {
    int UNCERTAIN = 0;
    int WALKING = 1;
    int RUNNING = 2;
    int CYCLING = 3;
}
