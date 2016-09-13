package com.egoriku.catsrunning.receivers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.ScamperActivity;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String ID_KEY = "ID_KEY";
    public static final String TEXT_REMINDER_KEY = "TEXT_REMINDER_KEY";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                extras.getInt(ID_KEY, 0),
                new Intent(context, ScamperActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_alarm_add_white)
                .setContentTitle(context.getResources().getString(R.string.alarm_notification_title))
                .setContentText(extras.getString(TEXT_REMINDER_KEY))
                .setAutoCancel(true);

        Notification notification = builder.build();
        notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.spaceship);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(
                extras.getInt(ID_KEY, 0),
                notification
        );
    }
}
