package com.egoriku.catsrunning.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;

public class AddReminderCommentDialogFragment extends DialogFragment {

    private String dialogTitle;
    private String dialogNegativeBtnText;
    private String dialogPositiveBtnText;
    private String dialogMessage;


    public static AddReminderCommentDialogFragment newInstance() {
        return new AddReminderCommentDialogFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogTitle = getResources().getString(R.string.add_reminder_dialog_title);
        dialogMessage = getResources().getString(R.string.add_reminder_dialog_message);
        dialogNegativeBtnText = getResources().getString(R.string.add_reminder_dialog_negative_btn_text);
        dialogPositiveBtnText = getResources().getString(R.string.add_reminder_dialog_positive_btn_text);
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_reminder_add_comment, null);
        final EditText editTextComment = (EditText) dialogView.findViewById(R.id.dialog_add_comment);

        return new AlertDialog.Builder(getContext())
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setView(dialogView)
                .setCancelable(false)
                .setIcon(App.getInstance().getResources().getDrawable(R.drawable.ic_filter_1_black))
                .setNegativeButton(dialogNegativeBtnText, null)
                .setPositiveButton(dialogPositiveBtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddReminderDateDialogFragment
                                .newInstance(editTextComment.getText().toString().trim())
                                .show(getFragmentManager(), null);
                    }
                })
                .create();
    }
}
