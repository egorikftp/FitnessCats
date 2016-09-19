package com.egoriku.catsrunning.fragments.dialogs;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.receivers.ReminderReceiver;

import java.util.Calendar;

public class AddReminderTimeDialogFragment extends DialogFragment {

    public static final String BROADCAST_ADD_NEW_REMINDER = "BROADCAST_ADD_NEW_REMINDER";
    private String dialogTitle;
    private String dialogNegativeBtnText;
    private String dialogPositiveBtnText;

    public AddReminderTimeDialogFragment() {
    }

    public static AddReminderTimeDialogFragment newInstance(long date, String textComment) {
        AddReminderTimeDialogFragment fragment = new AddReminderTimeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(RemindersFragment.KEY_UPDATE_REMINDER, date);
        bundle.putString(RemindersFragment.KEY_TEXT_REMINDER, textComment);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogTitle = getResources().getString(R.string.add_reminder_dialog_title_time);
        dialogNegativeBtnText = getResources().getString(R.string.add_reminder_dialog_negative_btn_text);
        dialogPositiveBtnText = getResources().getString(R.string.add_reminder_dialog_positive_btn_final);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_reminder_add_time, null);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.dialog_time_picker);
        final Calendar calendarDate = Calendar.getInstance();
        final Calendar allDateCalendar = Calendar.getInstance();

        calendarDate.setTimeInMillis(
                getArguments().getLong(RemindersFragment.KEY_UPDATE_REMINDER));

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minutes) {
                allDateCalendar.set(Calendar.HOUR_OF_DAY, hour);
                allDateCalendar.set(Calendar.MINUTE, minutes);
            }
        });

        return new AlertDialog.Builder(getContext())
                .setTitle(dialogTitle)
                .setView(dialogView)
                .setCancelable(false)
                .setIcon(App.getInstance().getResources().getDrawable(R.drawable.ic_filter_3_black))
                .setNegativeButton(dialogNegativeBtnText, null)
                .setPositiveButton(dialogPositiveBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setAlarm(
                                writeAlarmDB(allDateCalendar.getTimeInMillis() / 1000, getArguments().getString(RemindersFragment.KEY_TEXT_REMINDER)),
                                getArguments().getString(RemindersFragment.KEY_TEXT_REMINDER),
                                allDateCalendar.getTimeInMillis()
                        );

                        LocalBroadcastManager.getInstance(
                                App.getInstance()).sendBroadcastSync(new Intent(BROADCAST_ADD_NEW_REMINDER));
                    }
                }).create();
    }


    private void setAlarm(int id, String textReminder, long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) App.getInstance().getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                App.getInstance(),
                id,
                new Intent(App.getInstance(), ReminderReceiver.class)
                        .putExtra(ReminderReceiver.ID_KEY, id)
                        .putExtra(ReminderReceiver.TEXT_REMINDER_KEY, textReminder),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
            );
        }
    }


    private int writeAlarmDB(long dateReminderUnix, String textReminder) {
        int idAlarm;
        SQLiteStatement statement = App.getInstance().getDb().compileStatement(
                "INSERT INTO Reminder (dateReminder, textReminder) VALUES (?, ?)"
        );

        statement.bindLong(1, dateReminderUnix);
        statement.bindString(2, textReminder);

        try {
            idAlarm = (int) statement.executeInsert();
        } finally {
            statement.close();
        }
        return idAlarm;
    }
}
