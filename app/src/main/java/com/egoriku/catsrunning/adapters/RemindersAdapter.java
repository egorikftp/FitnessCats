package com.egoriku.catsrunning.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.interfaces.IRemindersClickListener;
import com.egoriku.catsrunning.models.ReminderModel;
import com.egoriku.catsrunning.utils.ConverterTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.egoriku.catsrunning.utils.VectorToDrawable.setImageAdapter;

public class RemindersAdapter extends AbstractAdapter<RemindersAdapter> {
    private List<ReminderModel> reminderModelList = new ArrayList<>();
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
        return reminderModelList.size();
    }


    @Override
    public void onBind(AbstractViewHolder holder, final RemindersAdapter remindersAdapter, final int position, int viewType) {
        Calendar calendar = Calendar.getInstance();

        if (calendar.getTimeInMillis() / 1000L > reminderModelList.get(position).getDateReminder()) {
            holder.<RelativeLayout>get(R.id.reminders_fragment_relative_layout)
                    .setBackgroundColor(Color.LTGRAY);
        }

        holder.<TextView>get(R.id.reminders_fragment_date)
                .setText(ConverterTime.convertDateReminder(reminderModelList.get(position).getDateReminder()));

        holder.<TextView>get(R.id.reminders_fragment_time)
                .setText(ConverterTime.convertTimeReminder(reminderModelList.get(position).getDateReminder()));

        holder.<ImageView>get(R.id.reminders_fragment_delete_reminder)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (iRemindersClickListener != null) {
                            iRemindersClickListener.onDeleteReminderClick(
                                    reminderModelList.get(position).getId(),
                                    position,
                                    reminderModelList.get(position).getTypeReminder()
                            );
                        }
                    }
                });

        holder.<TextView>get(R.id.reminders_fragment_time)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (iRemindersClickListener != null) {
                            iRemindersClickListener.onTimeReminderClick(
                                    reminderModelList.get(position).getId(),
                                    reminderModelList.get(position).getDateReminder(),
                                    reminderModelList.get(position).getTypeReminder(),
                                    position
                            );
                        }
                    }
                });

        holder.<TextView>get(R.id.reminders_fragment_date)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(iRemindersClickListener!=null){
                            iRemindersClickListener.onDateReminderClick(
                                    reminderModelList.get(position).getId(),
                                    reminderModelList.get(position).getDateReminder(),
                                    reminderModelList.get(position).getTypeReminder(),
                                    position
                            );
                        }
                    }
                });

        switch (reminderModelList.get(position).getTypeReminder()) {
            case 1:
                setImageAdapter(holder.<ImageView>get(R.id.reminders_fragment_image_type_reminder), R.drawable.ic_vec_directions_walk_reminders);
                break;

            case 2:
                setImageAdapter(holder.<ImageView>get(R.id.reminders_fragment_image_type_reminder), R.drawable.ic_vec_directions_run_reminders);
                break;

            case 3:
                setImageAdapter(holder.<ImageView>get(R.id.reminders_fragment_image_type_reminder), R.drawable.ic_vec_directions_bike_reminders);
                break;
        }
    }


    @Override
    public RemindersAdapter getItem(int position) {
        return null;
    }


    public void setiRemindersClickListener(IRemindersClickListener iRemindersClickListener) {
        this.iRemindersClickListener = iRemindersClickListener;
    }


    public void setData(List<ReminderModel> reminderModel) {
        this.reminderModelList = reminderModel;
        notifyDataSetChanged();
    }


    public void deletePositionData(int position) {
        reminderModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, reminderModelList.size());
    }


    public void addData(int position, ReminderModel reminderModel) {
        reminderModelList.add(position, reminderModel);
        notifyItemRangeInserted(position, reminderModelList.size());
        notifyDataSetChanged();
    }
}
