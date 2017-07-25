package com.egoriku.catsrunning.helpers;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({Events.CLICK, Events.LONG_CLICK, Events.LIKED_CLICK})
public @interface Events {
    String CLICK = "click";
    String LONG_CLICK = "long_click";
    String LIKED_CLICK = "liked_click";
}
