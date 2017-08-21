package com.egoriku.catsrunning.utils;

import android.content.Context;

import com.egoriku.catsrunning.activities.FitActivity;
import com.egoriku.catsrunning.models.FitState;

public class CustomChronometer implements Runnable {
    private Context context;
    private long startTime;
    private boolean isRunning;

    public CustomChronometer(Context context) {
        this.context = context;
    }


    public CustomChronometer(Context context, long startTime) {
        this.context = context;
        this.startTime = startTime;
    }


    public void startChronometer() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        isRunning = true;
    }


    public void stopChronometer() {
        isRunning = false;
    }


    public long getStartTime() {
        return startTime;
    }


    public boolean isRunning() {
        return isRunning;
    }


    @Override
    public void run() {
        while (isRunning) {
            long since = System.currentTimeMillis() - startTime;
            FitState.getInstance().setSinceTime(since);

            ((FitActivity) context).updateTimer(ConverterTime.ConvertTimeToString(since));

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
