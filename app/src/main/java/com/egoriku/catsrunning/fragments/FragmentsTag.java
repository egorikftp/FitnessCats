package com.egoriku.catsrunning.fragments;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({FragmentsTag.LIKED, FragmentsTag.REMINDER, FragmentsTag.MAIN, FragmentsTag.WHERE_I, FragmentsTag.STATISTIC, FragmentsTag.EXIT, FragmentsTag.SETTINGS})
public @interface FragmentsTag {
    String LIKED = "TAG_LIKED";
    String REMINDER = "TAG_REMINDERS";
    String MAIN = "TAG_MAIN";
    String STATISTIC = "TAG_STATISTIC";
    String WHERE_I = "WHERE_I";
    String EXIT = "TAG_EXIT_APP";
    String SETTINGS = "SETTINGS";
}
