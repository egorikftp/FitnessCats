package com.egoriku.catsrunning.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.interfaces.IRemindersClickListener;
import com.egoriku.catsrunning.models.ReminderModel;
import com.egoriku.catsrunning.utils.ConverterTime;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.utils.VectorToDrawable.getDrawable;

public class RemindersAdapter extends AbstractAdapter<RemindersAdapter> {
    private List<ReminderModel> reminderModel = new ArrayList<>();
    private IRemindersClickListener iRemindersClickListener;

    public RemindersAdapter() {
    }


    public void setOnItemClickListener(IRemindersClickListener iRemindersClickListener) {
        this.iRemindersClickListener = iRemindersClickListener;
    }


    @Override
    public AbstractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_fragment_reminders, parent, false);
        return new AbstractViewHolder(inflater);
    }


    @Override
    public int getItemCount() {
        return reminderModel.size();
    }


    @Override
    public void onBind(AbstractViewHolder holder, RemindersAdapter remindersAdapter, final int position, int viewType) {
        holder.<TextView>get(R.id.reminders_fragment_comment)
                .setText(reminderModel.get(position).getTextReminder());

        holder.<TextView>get(R.id.reminders_fragment_date_time)
                .setText(ConverterTime.convertUnixDate(reminderModel.get(position).getDateReminder()));

        holder.<ImageView>get(R.id.reminders_fragment_delete_reminder)
                .setImageDrawable(getDrawable(R.drawable.ic_vec_clear_black_24dp));

        holder.<ImageView>get(R.id.reminders_fragment_delete_reminder)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (iRemindersClickListener != null) {
                            iRemindersClickListener.onDeleteReminderClick(
                                    reminderModel.get(position).getId(),
                                    reminderModel.get(position).getTextReminder(),
                                    position
                            );
                        }
                    }
                });

        holder.<TextView>get(R.id.reminders_fragment_comment)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (iRemindersClickListener != null) {
                            iRemindersClickListener.onCommentReminderClick(
                                    reminderModel.get(position).getId(),
                                    reminderModel.get(position).getDateReminder(),
                                    reminderModel.get(position).getTextReminder(),
                                    position
                            );
                        }
                    }
                });

        holder.<TextView>get(R.id.reminders_fragment_date_time)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (iRemindersClickListener != null) {
                            iRemindersClickListener.onDateReminderClick(
                                    reminderModel.get(position).getId(),
                                    reminderModel.get(position).getDateReminder(),
                                    reminderModel.get(position).getTextReminder(),
                                    position
                            );
                        }
                    }
                });
    }


    @Override
    public RemindersAdapter getItem(int position) {
        return null;
    }


    public void setiRemindersClickListener(IRemindersClickListener iRemindersClickListener) {
        this.iRemindersClickListener = iRemindersClickListener;
    }


    public void setData(List<ReminderModel> reminderModel) {
        this.reminderModel = reminderModel;
        notifyDataSetChanged();
    }
}
