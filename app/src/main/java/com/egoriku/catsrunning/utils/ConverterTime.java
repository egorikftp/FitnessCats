package com.egoriku.catsrunning.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ConverterTime {
    public static final long MILLIS_TO_MINUTES = 60000;
    public static final long MILLS_TO_HOURS = 3600000;


    public static String ConvertTimeToString(long timeInMillis) {
        int seconds = (int) (timeInMillis / 1000) % 60;
        int minutes = (int) ((timeInMillis / (MILLIS_TO_MINUTES)) % 60);
        int hours = (int) ((timeInMillis / (MILLS_TO_HOURS)) % 24);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    public static String ConvertTimeAllFitnessData(long timeInMillis, long beginsAt) {
        Date date = new Date(timeInMillis * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String startTime = simpleDateFormat.format(date);

        int minutes = (int) ((beginsAt / (MILLIS_TO_MINUTES)) % 60);
        int hours = (int) ((beginsAt / (MILLS_TO_HOURS)) % 24);
        //String endTime = String.format("%02d:%02d", hours, minutes);
        String endTime = simpleDateFormat.format(new Date((timeInMillis + beginsAt / 1000L) * 1000L));

        Log.e("time", String.valueOf(timeInMillis));
        Log.e("begins", String.valueOf(beginsAt + timeInMillis));

        return startTime + " - " + endTime;
    }


    public static String convertDateReminder(long someDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(new Date(someDate * 1000L));
    }


    public static String convertTimeReminder(long someDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(new Date(someDate * 1000L));
    }


    public static String convertUnixDateWithoutHours(long someDate) {
        Date date = new Date(someDate * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }
}
