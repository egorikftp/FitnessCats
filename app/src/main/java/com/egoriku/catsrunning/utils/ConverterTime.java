package com.egoriku.catsrunning.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConverterTime {
    public static final long MILLIS_TO_MINUTES = 60000;
    public static final long MILLS_TO_HOURS = 3600000;


    public static String ConvertTimeToStringWithMill(long timeInMillis) {
        int millis = (int) timeInMillis % 100;
        int seconds = (int) (timeInMillis / 1000) % 60;
        int minutes = (int) ((timeInMillis / (MILLIS_TO_MINUTES)) % 60);
        int hours = (int) ((timeInMillis / (MILLS_TO_HOURS)) % 24); //сбрасывает на 0 после 24 часов
        return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, millis);
    }


    public static String ConvertTimeToString(long timeInMillis) {
        int seconds = (int) (timeInMillis / 1000) % 60;
        int minutes = (int) ((timeInMillis / (MILLIS_TO_MINUTES)) % 60);
        int hours = (int) ((timeInMillis / (MILLS_TO_HOURS)) % 24);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    public static String convertUnixDate(long someDate) {
        Date date = new Date(someDate * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));
        return simpleDateFormat.format(date);
    }
}
