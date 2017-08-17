package com.egoriku.catsrunning.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.interfaces.IRemindersClickListener;
import com.egoriku.catsrunning.models.ReminderModel;
import com.egoriku.catsrunning.utils.TimeUtil;
import com.egoriku.core_lib.AbstractAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.egoriku.catsrunning.kt_util.DrawableKt.drawableCompat;
import static com.egoriku.catsrunning.kt_util.DrawableKt.drawableTypeReminder;

public class RemindersAdapter extends AbstractAdapter<RemindersAdapter> {

    public static final float ANIMATE_ALPHA = 0.0f;
    public static final int ANIMATE_DURATION = 300;
    private static final float ANIMATE_ALPHA_MORE = 1.0f;
    private List<ReminderModel> reminderModelList = new ArrayList<>();
    private IRemindersClickListener iRemindersClickListener;
    private Context context;

    public RemindersAdapter(Context context) {
        this.context = context;
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
    public void onBind(final AbstractViewHolder holder, final RemindersAdapter remindersAdapter, final int position, int viewType) {
        Calendar calendar = Calendar.getInstance();
        final RelativeLayout relativeLayout = holder.get(R.id.reminders_fragment_relative_layout);
        final TextView textViewDate = holder.get(R.id.reminders_fragment_date);
        final TextView textViewTime = holder.get(R.id.reminders_fragment_time);
        final ImageView imageViewDelete = holder.get(R.id.reminders_fragment_delete_reminder);
        final ImageButton imageBtnExpand = holder.get(R.id.reminders_fragment_image_expand_info);
        final ImageView imageViewLine = holder.get(R.id.reminders_fragment_static_line);
        final ImageView imageViewLiked = holder.get(R.id.reminders_fragment_image_type_reminder);
        final Switch aSwitch = holder.get(R.id.reminder_fragment_switch);

        if (calendar.getTimeInMillis() / 1000L > reminderModelList.get(position).getDateReminder()) {
            relativeLayout.setBackgroundColor(Color.LTGRAY);
        }

        textViewDate.setText(TimeUtil.convertDateReminder(reminderModelList.get(position).getDateReminder()));
        textViewTime.setText(TimeUtil.convertTimeReminder(reminderModelList.get(position).getDateReminder()));

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
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

        textViewTime.setOnClickListener(new View.OnClickListener() {
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

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iRemindersClickListener != null) {
                    iRemindersClickListener.onDateReminderClick(
                            reminderModelList.get(position).getId(),
                            reminderModelList.get(position).getDateReminder(),
                            reminderModelList.get(position).getTypeReminder(),
                            position
                    );
                }
            }
        });

        if (imageViewLine.getVisibility() == View.VISIBLE) {
            animateItemExpandLess(imageViewLine, imageViewDelete);
        }

        imageBtnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageViewLine.getVisibility() == View.GONE) {
                    animateItemExpandMore(imageViewLine, imageViewDelete);
                    imageBtnExpand.setImageDrawable(drawableCompat(context, R.drawable.ic_vec_expand_less_black));
                } else {
                    animateItemExpandLess(imageViewLine, imageViewDelete);
                    imageBtnExpand.setImageDrawable(drawableCompat(context, R.drawable.ic_vec_expand_more_black));
                }
            }
        });

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iRemindersClickListener != null) {
                    iRemindersClickListener.onSwitcherReminderClick(
                            reminderModelList.get(position).getId(),
                            reminderModelList.get(position).getDateReminder(),
                            reminderModelList.get(position).getTypeReminder(),
                            position,
                            aSwitch.isChecked()
                    );
                }
            }
        });

        imageViewLiked.setImageDrawable(drawableTypeReminder(context, reminderModelList.get(position).getTypeReminder()));

        switch (reminderModelList.get(position).getIsRing()) {
            case 0:
                aSwitch.setChecked(false);
                break;

            case 1:
                aSwitch.setChecked(true);
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


    private void animateItemExpandLess(final ImageView imageViewLine, final ImageView imageViewDelete) {
        imageViewLine.animate()
                .alpha(ANIMATE_ALPHA)
                .setDuration(ANIMATE_DURATION)
                .translationY(-imageViewLine.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        imageViewLine.setVisibility(View.GONE);
                    }
                });

        imageViewDelete.animate()
                .alpha(ANIMATE_ALPHA)
                .setDuration(ANIMATE_DURATION)
                .translationY(-imageViewDelete.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        imageViewDelete.setVisibility(View.GONE);
                    }
                });
    }


    private void animateItemExpandMore(final ImageView imageViewLine, final ImageView imageViewDelete) {
        imageViewLine.animate()
                .translationYBy(imageViewLine.getHeight())
                .alpha(ANIMATE_ALPHA_MORE)
                .setDuration(ANIMATE_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationEnd(animation);
                        imageViewLine.setVisibility(View.VISIBLE);
                    }
                });

        imageViewDelete.animate()
                .alpha(ANIMATE_ALPHA_MORE)
                .setDuration(ANIMATE_DURATION)
                .translationYBy(imageViewDelete.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        imageViewDelete.setVisibility(View.VISIBLE);
                    }
                });
    }
}
