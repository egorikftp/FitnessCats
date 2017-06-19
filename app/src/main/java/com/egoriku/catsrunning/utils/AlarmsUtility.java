package com.egoriku.catsrunning.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.receivers.ReminderReceiver;

import static com.egoriku.catsrunning.models.Constants.Broadcast.BROADCAST_ADD_NEW_REMINDER;
import static com.egoriku.catsrunning.models.Constants.Extras.EXTRA_ID_REMINDER_KEY;
import static com.egoriku.catsrunning.models.Constants.Extras.EXTRA_TEXT_TYPE_REMINDER_KEY;
import static com.egoriku.catsrunning.models.Constants.Extras.TYPE_REMINDER_KEY;

public class AlarmsUtility {

    public static void setAlarm(int id, String typeReminderText, long timeInMillis, int typeReminder) {
        AlarmManager alarmManager = (AlarmManager) App.appInstance.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(
                        App.appInstance,
                        id,
                        new Intent(App.appInstance, ReminderReceiver.class)
                                .putExtra(EXTRA_ID_REMINDER_KEY, id)
                                .putExtra(EXTRA_TEXT_TYPE_REMINDER_KEY, typeReminderText)
                                .putExtra(TYPE_REMINDER_KEY, typeReminder),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }

        LocalBroadcastManager.getInstance(App.appInstance).sendBroadcastSync(new Intent(BROADCAST_ADD_NEW_REMINDER));
    }
}
