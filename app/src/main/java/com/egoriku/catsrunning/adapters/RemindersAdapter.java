package com.egoriku.catsrunning.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.interfaces.IRecyclerViewRemindersListener;
import com.egoriku.catsrunning.models.ReminderModel;
import com.egoriku.catsrunning.utils.ConverterTime;

import java.util.ArrayList;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder> {
    private static final String TAG_VIEW_COMMENT = "comment";
    private static final String TAG_VIEW_DATE = "date";

    private ArrayList<ReminderModel> reminderModels;
    public static IRecyclerViewRemindersListener iRecyclerViewRemindersListener;

    public RemindersAdapter(ArrayList<ReminderModel> models) {
        this.reminderModels = models;
    }

    @Override
    public RemindersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_fragment_reminders, parent, false);

        return new ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(RemindersAdapter.ViewHolder holder, int position) {
        holder.reminderComment.setText(reminderModels.get(position).getTextReminder());
        holder.reminderDate.setText(ConverterTime.convertUnixDate(reminderModels.get(position).getDateReminder()));
        holder.deleteReminder.setImageDrawable(App.getInstance().getResources().getDrawable(R.drawable.ic_clear_black));
    }


    @Override
    public int getItemCount() {
        return reminderModels.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView reminderComment;
        public TextView reminderDate;
        public ImageView deleteReminder;


        public ViewHolder(View itemView) {
            super(itemView);
            reminderComment = (TextView) itemView.findViewById(R.id.reminders_fragment_comment);
            reminderDate = (TextView) itemView.findViewById(R.id.reminders_fragment_date_time);
            deleteReminder = (ImageView) itemView.findViewById(R.id.reminders_fragment_delete_reminder);

            reminderComment.setOnClickListener(this);
            reminderDate.setOnClickListener(this);
            deleteReminder.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (view instanceof ImageView) {
                iRecyclerViewRemindersListener.onDeleteReminderClick(getPosition());
            } else {
                if (view.getTag().equals(TAG_VIEW_COMMENT)) {
                    iRecyclerViewRemindersListener.onCommentReminderClick(getPosition());
                }

                if (view.getTag().equals(TAG_VIEW_DATE)) {
                    iRecyclerViewRemindersListener.onDateReminderClick(getPosition());
                }
            }
        }
    }


    public void setOnItemClickListener(IRecyclerViewRemindersListener listener) {
        this.iRecyclerViewRemindersListener = listener;
    }
}
