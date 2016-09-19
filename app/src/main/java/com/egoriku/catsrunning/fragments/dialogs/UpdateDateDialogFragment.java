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
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.receivers.ReminderReceiver;

import java.util.Calendar;
import java.util.Date;

public class UpdateDateDialogFragment extends DialogFragment {

    public static final String BROADCAST_UPDATE_REMINDER_DATE = "BROADCAST_UPDATE_REMINDER_DATE";
    private String dialogTitle;
    private String dialogNegativeBtnText;
    private String dialogPositiveBtnText;
    private String dialogMessage;


    public UpdateDateDialogFragment() {
    }


    public static UpdateDateDialogFragment newInstance(int id, String textReminder, int timeInMillis) {
        UpdateDateDialogFragment updateDateDialogFragment = new UpdateDateDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(RemindersFragment.KEY_ID, id);
        bundle.putString(RemindersFragment.KEY_TEXT_REMINDER, textReminder);
        bundle.putInt(RemindersFragment.KEY_UPDATE_REMINDER, timeInMillis);
        updateDateDialogFragment.setArguments(bundle);

        return updateDateDialogFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogTitle = getResources().getString(R.string.date_dialog_title);
        dialogNegativeBtnText = getResources().getString(R.string.date_dialog_negative_btn_text);
        dialogPositiveBtnText = getResources().getString(R.string.date_dialog_positive_btn_text);
        dialogMessage = getResources().getString(R.string.date_dialog_message);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_date_update, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.update_dialog_date_picker);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.update_dialog_time_picker);
        final Calendar reminderTime = Calendar.getInstance();
        final Calendar oldDate = Calendar.getInstance();

        oldDate.setTime(new Date(getArguments().getInt(RemindersFragment.KEY_UPDATE_REMINDER) * 1000L));

        if (android.os.Build.VERSION.SDK_INT >= 15 && android.os.Build.VERSION.SDK_INT <= 19) {
            datePicker.setCalendarViewShown(false);
        }

        datePicker.updateDate(oldDate.get(Calendar.YEAR), oldDate.get(Calendar.MONTH), oldDate.get(Calendar.DAY_OF_MONTH));
        timePicker.setCurrentHour(oldDate.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(oldDate.get(Calendar.MINUTE));
        timePicker.setIs24HourView(true);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minutes) {
                reminderTime.set(Calendar.HOUR_OF_DAY, hour);
                reminderTime.set(Calendar.MINUTE, minutes);
            }
        });

        return new AlertDialog.Builder(getContext())
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setView(dialogView)
                .setNegativeButton(dialogNegativeBtnText, null)
                .setPositiveButton(dialogPositiveBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reminderTime.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                        updateDB(
                                reminderTime.getTimeInMillis() / 1000,
                                getArguments().getInt(RemindersFragment.KEY_ID)
                        );

                        setAlarm(
                                getArguments().getInt(RemindersFragment.KEY_ID),
                                getArguments().getString(RemindersFragment.KEY_TEXT_REMINDER),
                                reminderTime.getTimeInMillis()
                        );

                        LocalBroadcastManager.getInstance(
                                App.getInstance()).sendBroadcastSync(new Intent(BROADCAST_UPDATE_REMINDER_DATE));
                    }
                }).show();
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


    private void updateDB(long dateReminderUnix, int id) {
        SQLiteStatement sqLiteStatement = App.getInstance().getDb().compileStatement(
                "UPDATE Reminder SET dateReminder = ? WHERE _id = ?"
        );

        sqLiteStatement.bindLong(1, dateReminderUnix);
        sqLiteStatement.bindLong(2, id);

        try {
            sqLiteStatement.execute();
        } finally {
            sqLiteStatement.close();
        }
    }
}
