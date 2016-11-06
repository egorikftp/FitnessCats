package com.egoriku.catsrunning.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.fragments.RemindersFragment;

import java.util.Calendar;

import static com.egoriku.catsrunning.helpers.DbActions.updateReminder;
import static com.egoriku.catsrunning.utils.AlarmsUtills.setAlarm;
import static com.egoriku.catsrunning.utils.TypeFitBuilder.getTypeFit;

public class UpdateDateReminderDialog extends DialogFragment {
    public static final String BROADCAST_UPDATE_REMINDER_DATE = "BROADCAST_UPDATE_REMINDER_DATE";
    private String dialogTitle;
    private String dialogNegativeBtnText;
    private String dialogPositiveBtnText;


    public UpdateDateReminderDialog() {
    }


    public static UpdateDateReminderDialog newInstance(int id, long dateReminder, int typeReminder) {
        UpdateDateReminderDialog updateDateDialogFragment = new UpdateDateReminderDialog();

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
        dialogTitle = getResources().getString(R.string.date_dialog_title);
        dialogNegativeBtnText = getResources().getString(R.string.date_dialog_negative_btn_text);
        dialogPositiveBtnText = getResources().getString(R.string.date_dialog_positive_btn_text);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_date_update, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.update_dialog_date_picker);
        final Calendar reminderDate = Calendar.getInstance();

        reminderDate.setTimeInMillis(getArguments().getLong(RemindersFragment.KEY_DATE_REMINDER) * 1000L);

        if (android.os.Build.VERSION.SDK_INT >= 15 && android.os.Build.VERSION.SDK_INT <= 19) {
            datePicker.setCalendarViewShown(false);
        }

        datePicker.init(reminderDate.get(Calendar.YEAR), reminderDate.get(Calendar.MONTH), reminderDate.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                reminderDate.set(year, monthOfYear, dayOfMonth);
            }
        });

        return new AlertDialog.Builder(getContext())
                .setTitle(dialogTitle)
                .setView(dialogView)
                .setNegativeButton(dialogNegativeBtnText, null)
                .setPositiveButton(dialogPositiveBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateReminder(reminderDate.getTimeInMillis() / 1000, getArguments().getInt(RemindersFragment.KEY_ID));

                        setAlarm(
                                getArguments().getInt(RemindersFragment.KEY_ID),
                                getTypeFit(getArguments().getInt(RemindersFragment.KEY_TYPE_REMINDER), false, R.array.type_reminder),
                                reminderDate.getTimeInMillis(),
                                getArguments().getInt(RemindersFragment.KEY_TYPE_REMINDER)
                        );

                        LocalBroadcastManager.getInstance(App.getInstance())
                                .sendBroadcastSync(new Intent(BROADCAST_UPDATE_REMINDER_DATE));
                    }
                }).show();
    }
}
