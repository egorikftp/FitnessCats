package com.egoriku.catsrunning.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.fragments.RemindersFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddReminderDateDialogFragment extends DialogFragment {

    private String dialogTitle;
    private String dialogNegativeBtnText;
    private String dialogPositiveBtnText;


    public AddReminderDateDialogFragment() {
    }


    public static AddReminderDateDialogFragment newInstance(String textComment) {
        AddReminderDateDialogFragment fragment = new AddReminderDateDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(RemindersFragment.KEY_TEXT_REMINDER, textComment);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogTitle = getResources().getString(R.string.add_reminder_dialog_title_date);
        dialogNegativeBtnText = getResources().getString(R.string.add_reminder_dialog_negative_btn_text);
        dialogPositiveBtnText = getResources().getString(R.string.add_reminder_dialog_positive_btn_text);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_reminder_add_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.dialog_date_picker);
        final Calendar calendarDate = Calendar.getInstance();

        if (android.os.Build.VERSION.SDK_INT >= 15 && android.os.Build.VERSION.SDK_INT <= 19) {
            datePicker.setCalendarViewShown(false);
        }

        return new AlertDialog.Builder(getContext())
                .setTitle(dialogTitle)
                .setView(dialogView)
                .setCancelable(false)
                .setIcon(App.getInstance().getResources().getDrawable(R.drawable.ic_filter_2_black))
                .setNegativeButton(dialogNegativeBtnText, null)
                .setPositiveButton(dialogPositiveBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        calendarDate.set(
                                datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth()
                        );

                        AddReminderTimeDialogFragment
                                .newInstance(
                                        calendarDate.getTimeInMillis(),
                                        getArguments().getString(RemindersFragment.KEY_TEXT_REMINDER))
                                .show(getFragmentManager(), null);
                    }
                }).show();
    }

    private long convertStringToUnixTime() throws ParseException {
        String dateStr = "00:20:03:19";
        long unixTime;

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        unixTime = formatter.parse(dateStr).getTime();

        unixTime = unixTime / 1000;

        return unixTime;
    }
}
