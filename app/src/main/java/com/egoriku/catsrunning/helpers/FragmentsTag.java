package com.egoriku.catsrunning.helpers;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({FragmentsTag.LIKED, FragmentsTag.REMINDER, FragmentsTag.MAIN, FragmentsTag.STATISTIC, FragmentsTag.EXIT, FragmentsTag.SETTINGS, FragmentsTag.NEW_MAIN})
public @interface FragmentsTag {
    String LIKED = "TAG_LIKED";
    String REMINDER = "TAG_REMINDERS";
    String MAIN = "TAG_MAIN";
    String STATISTIC = "TAG_STATISTIC";
    String EXIT = "TAG_EXIT_APP";
    String SETTINGS = "SETTINGS";
    String NEW_MAIN = "NEW_MAIN";
}
