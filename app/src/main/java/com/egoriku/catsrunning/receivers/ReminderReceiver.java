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

import static com.egoriku.catsrunning.models.State.EXTRA_ID_REMINDER_KEY;
import static com.egoriku.catsrunning.models.State.EXTRA_TEXT_TYPE_REMINDER_KEY;
import static com.egoriku.catsrunning.models.State.KEY_TYPE_FIT;
import static com.egoriku.catsrunning.models.State.TYPE_REMINDER_KEY;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String ANDROID_RESOURCE = "android.resource://";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                extras.getInt(EXTRA_ID_REMINDER_KEY, 0),
                new Intent(context, ScamperActivity.class).putExtra(KEY_TYPE_FIT, extras.getInt(TYPE_REMINDER_KEY)),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_alarm_notification)
                .setContentTitle(context.getResources().getString(R.string.alarm_notification_title))
                .setContentText(extras.getString(EXTRA_TEXT_TYPE_REMINDER_KEY))
                .setAutoCancel(true)
                .setVibrate(new long[]{ 100, 300, 500, 300});

        Notification notification = builder.build();
        notification.sound = Uri.parse(ANDROID_RESOURCE + context.getPackageName() + "/" + R.raw.spaceship);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(extras.getInt(EXTRA_ID_REMINDER_KEY, 0), notification);
    }
}
