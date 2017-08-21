package com.egoriku.catsrunning.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.egoriku.catsrunning.models.Constants.FormateDate.FORMAT_DATE;
import static com.egoriku.catsrunning.models.Constants.FormateDate.FORMAT_DATE_FULL;
import static com.egoriku.catsrunning.models.Constants.FormateDate.FORMAT_DATE_LOW;
import static com.egoriku.catsrunning.models.Constants.FormateDate.FORMAT_TIME;

public class TimeUtil {
    private static final long MILLIS_TO_MINUTES = 60000;
    private static final long MILLS_TO_HOURS = 3600000;

    @Deprecated
    public static String ConvertTimeToString(long timeInMillis) {
        int seconds = (int) (timeInMillis / 1000) % 60;
        int minutes = (int) ((timeInMillis / (MILLIS_TO_MINUTES)) % 60);
        int hours = (int) ((timeInMillis / (MILLS_TO_HOURS)) % 24);
        return String.format(Locale.getDefault(), FORMAT_DATE, hours, minutes, seconds);
    }


    public static String ConvertTimeAllFitnessData(long beginsAt, long timeFit) {
        Date date = new Date(beginsAt * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_TIME, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String startTime = simpleDateFormat.format(date);

        if (timeFit == 0) {
            return startTime + " - ?";
        }

        String endTime = simpleDateFormat.format(new Date((beginsAt + timeFit / 1000L) * 1000L));
        return startTime + " - " + endTime;
    }

    public static String getTime(long timeInMillis) {
        String time = "-";

        if (timeInMillis == 0) {
            return time;
        } else {
            int seconds = (int) (timeInMillis / 1000) % 60;
            int minutes = (int) ((timeInMillis / (MILLIS_TO_MINUTES)) % 60);
            int hours = (int) ((timeInMillis / (MILLS_TO_HOURS)) % 24);
            return String.format(Locale.getDefault(), FORMAT_DATE, hours, minutes, seconds);
        }
    }

    public static String convertDateReminder(long someDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE_LOW, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(new Date(someDate * 1000L));
    }


    public static String convertTimeReminder(long someDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_TIME, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(new Date(someDate * 1000L));
    }


    public static String convertUnixDateWithoutHours(long someDate) {
        Date date = new Date(someDate * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE_FULL, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }
}
