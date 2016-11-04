package com.egoriku.catsrunning.dialogs;

import android.app.Dialog;
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

import java.util.Calendar;

import static com.egoriku.catsrunning.utils.AlarmsUtills.setAlarm;
import static com.egoriku.catsrunning.utils.TypeFitBuilder.getTypeFit;

public class UpdateTimeReminderDialog extends DialogFragment {
    public static final String BROADCAST_UPDATE_REMINDER_TIME = "BROADCAST_UPDATE_REMINDER_TIME";
    private String dialogTitle;
    private String dialogNegativeBtnText;
    private String dialogPositiveBtnText;


    public UpdateTimeReminderDialog() {
    }


    public static UpdateTimeReminderDialog newInstance(int id, long dateReminder, int typeReminder) {
        UpdateTimeReminderDialog updateDateDialogFragment = new UpdateTimeReminderDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(RemindersFragment.KEY_ID, id);
        bundle.putLong(RemindersFragment.KEY_DATE_REMINDER, dateReminder);
        bundle.putInt(RemindersFragment.KEY_TYPE_REMINDER, typeReminder);
        updateDateDialogFragment.setArguments(bundle);
        return updateDateDialogFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogTitle = getResources().getString(R.string.time_dialog_title);
        dialogNegativeBtnText = getResources().getString(R.string.date_dialog_negative_btn_text);
        dialogPositiveBtnText = getResources().getString(R.string.date_dialog_positive_btn_text);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_time_update, null);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.update_dialog_time_picker);
        final Calendar reminderTime = Calendar.getInstance();

        reminderTime.setTimeInMillis(getArguments().getLong(RemindersFragment.KEY_DATE_REMINDER) * 1000L);
        timePicker.setIs24HourView(true);

        if (Build.VERSION.SDK_INT >= 15 && Build.VERSION.SDK_INT <= 22) {
            timePicker.setCurrentHour(reminderTime.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(reminderTime.get(Calendar.MINUTE));
        } else {
            timePicker.setHour(reminderTime.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(reminderTime.get(Calendar.MINUTE));
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minutes) {
                reminderTime.set(Calendar.HOUR_OF_DAY, hour);
                reminderTime.set(Calendar.MINUTE, minutes);
            }
        });

        return new AlertDialog.Builder(getContext())
                .setTitle(dialogTitle)
                .setView(dialogView)
                .setNegativeButton(dialogNegativeBtnText, null)
                .setPositiveButton(dialogPositiveBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateDB(
                                reminderTime.getTimeInMillis() / 1000,
                                getArguments().getInt(RemindersFragment.KEY_ID)
                        );

                        setAlarm(
                                getArguments().getInt(RemindersFragment.KEY_ID),
                                getTypeFit(getArguments().getInt(RemindersFragment.KEY_TYPE_REMINDER), false, R.array.type_reminder),
                                reminderTime.getTimeInMillis(),
                                getArguments().getInt(RemindersFragment.KEY_TYPE_REMINDER)
                        );

                        LocalBroadcastManager.getInstance(App.getInstance())
                                .sendBroadcastSync(new Intent(BROADCAST_UPDATE_REMINDER_TIME));

                    }
                }).show();
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
