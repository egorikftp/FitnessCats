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
import android.widget.EditText;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.fragments.RemindersFragment;
import com.egoriku.catsrunning.receivers.ReminderReceiver;

public class UpdateCommentDialogFragment extends DialogFragment {

    public static final String BROADCAST_UPDATE_REMINDER_COMMENT = "BROADCAST_UPDATE_REMINDER_COMMENT";
    private String dialogTitle;
    private String dialogNegativeBtnText;
    private String dialogPositiveBtnText;
    private String dialogMessage;


    public UpdateCommentDialogFragment() {
    }

    public static UpdateCommentDialogFragment newInstance(int id, String textReminder, long timeInMillis) {
        UpdateCommentDialogFragment updateCommentDialogFragment = new UpdateCommentDialogFragment();

        Bundle args = new Bundle();
        args.putInt(RemindersFragment.KEY_ID, id);
        args.putString(RemindersFragment.KEY_TYPE_REMINDER, textReminder);
        args.putLong(RemindersFragment.KEY_UPDATE_REMINDER, timeInMillis);
        updateCommentDialogFragment.setArguments(args);

        return updateCommentDialogFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogTitle = getResources().getString(R.string.comment_dialog_title);
        dialogNegativeBtnText = getResources().getString(R.string.add_reminder_part_one_dialog_negative_btn);
        dialogPositiveBtnText = getResources().getString(R.string.comment_dialog_positive_btn_text);
        dialogMessage = getResources().getString(R.string.comment_dialog_positive_btn_text);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_comment_update, null);
        final EditText editTextComment = (EditText) dialogView.findViewById(R.id.edit_comment_dialog);

        if (getArguments() != null) {
            editTextComment.setText(getArguments().getString(RemindersFragment.KEY_TYPE_REMINDER));
            editTextComment.setSelection(editTextComment.getText().length());
        }

        return new AlertDialog.Builder(getContext())
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setView(dialogView)
                .setIcon(App.getInstance().getResources().getDrawable(R.mipmap.ic_launcher))
                .setNegativeButton(dialogNegativeBtnText, null)
                .setPositiveButton(dialogPositiveBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDb(
                                editTextComment.getText().toString(),
                                getArguments().getInt(RemindersFragment.KEY_ID)
                        );

                        updateAlarmTitle(
                                getArguments().getInt(RemindersFragment.KEY_ID),
                                editTextComment.getText().toString(),
                                getArguments().getLong(RemindersFragment.KEY_UPDATE_REMINDER)
                        );

                        LocalBroadcastManager.getInstance(
                                App.getInstance()).sendBroadcastSync(new Intent(BROADCAST_UPDATE_REMINDER_COMMENT)
                        );
                    }
                })
                .create();
    }


    private void updateDb(String comment, int position) {
        SQLiteStatement statement = App.getInstance().getDb().compileStatement(
                "UPDATE Reminder SET textReminder = ? WHERE _id = ?"
        );

        statement.bindString(1, comment);
        statement.bindLong(2, position);

        try {
            statement.execute();
        } finally {
            statement.close();
        }
    }


    private void updateAlarmTitle(int id, String textReminder, long timeInMillis) {
        timeInMillis = timeInMillis * 1000;

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
}
