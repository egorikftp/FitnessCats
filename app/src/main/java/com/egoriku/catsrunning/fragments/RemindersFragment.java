package com.egoriku.catsrunning.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.AddReminderActivity;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.egoriku.catsrunning.adapters.RemindersAdapter;
import com.egoriku.catsrunning.adapters.interfaces.IRemindersClickListener;
import com.egoriku.catsrunning.dialogs.UpdateDateReminderDialog;
import com.egoriku.catsrunning.dialogs.UpdateTimeReminderDialog;
import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.models.ReminderModel;
import com.egoriku.catsrunning.receivers.ReminderReceiver;

import java.util.ArrayList;
import java.util.Calendar;

import static com.egoriku.catsrunning.helpers.DbActions.deleteReminderDb;
import static com.egoriku.catsrunning.models.State.DATE_REMINDER;
import static com.egoriku.catsrunning.models.State.EXTRA_ID_REMINDER_KEY;
import static com.egoriku.catsrunning.models.State.EXTRA_TEXT_TYPE_REMINDER_KEY;
import static com.egoriku.catsrunning.models.State.TABLE_REMINDER;
import static com.egoriku.catsrunning.models.State.TYPE_REMINDER;
import static com.egoriku.catsrunning.models.State._ID;
import static com.egoriku.catsrunning.utils.TypeFitBuilder.getTypeFit;

public class RemindersFragment extends Fragment {
    public static final String TAG_REMINDERS_FRAGMENT = "TAG_REMINDERS_FRAGMENT";
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_TYPE_REMINDER = "KEY_TYPE_REMINDER";
    public static final String KEY_DATE_REMINDER = "KEY_DATE_REMINDER";
    private static final int UNICODE_EMOJI = 0x1F638;

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private ImageView imageViewNoTracks;
    private TextView noReminders;

    private ArrayList<ReminderModel> reminderModel;
    private RemindersAdapter remindersAdapter;

    private BroadcastReceiver broadcastUpdateTime = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showReminders();
        }
    };
    private BroadcastReceiver broadcastUpdateDate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showReminders();
        }
    };


    public RemindersFragment() {
    }

    public static RemindersFragment newInstance() {
        return new RemindersFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TracksActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_reminders, TAG_REMINDERS_FRAGMENT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reminderModel = new ArrayList<>();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.reminders_recycler_view);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.reminders_fab_add);
        noReminders = (TextView) view.findViewById(R.id.reminders_fragment_no_more_reminders);
        imageViewNoTracks = (ImageView) view.findViewById(R.id.fragment_fitness_data_image);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.reminders_appbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.reminders_fragment_collapsing_toolbar);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddReminderActivity.class));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(App.getInstance()));
        remindersAdapter = new RemindersAdapter();
        return view;
    }


    private void showReminders() {
        noReminders.setText(null);
        getRemindersFromDb();

        if (reminderModel.size() == 0) {
            setScrollingEnabled(false);
            imageViewNoTracks.setVisibility(View.VISIBLE);
            noReminders.setText(String.format((String) getResources().getText(R.string.reminders_fragment_no_more_reminders), getEmojiByUnicode(UNICODE_EMOJI)));
            animateView();
        } else {
            setScrollingEnabled(true);
            imageViewNoTracks.setVisibility(View.GONE);

            remindersAdapter.setData(reminderModel);
            recyclerView.setAdapter(remindersAdapter);

            remindersAdapter.setOnItemClickListener(new IRemindersClickListener() {
                @Override
                public void onDeleteReminderClick(final int id, final int position, final int typeFit) {
                    if (reminderModel.get(position).getDateReminder() < Calendar.getInstance().getTimeInMillis() / 1000L) {
                        cancelAlarm(id, getTypeFit(typeFit, true, R.array.type_reminder));
                        deleteReminderDb(id);
                        remindersAdapter.deletePositionData(position);

                        if (reminderModel.size() == 0) {
                            showReminders();
                        }
                    } else {
                        showSnackBar(position, typeFit, R.string.reminders_fragment_snackbar_alarm_delete, R.string.reminders_fragment_snackbar_revert);
                    }
                }

                @Override
                public void onTimeReminderClick(int id, long dateReminder, int typeReminder, int position) {
                    UpdateTimeReminderDialog.newInstance(id, dateReminder, typeReminder).show(getFragmentManager(), null);
                }

                @Override
                public void onDateReminderClick(int id, long dateReminder, int typeReminder, int position) {
                    UpdateDateReminderDialog.newInstance(id, dateReminder, typeReminder).show(getFragmentManager(), null);
                }
            });
        }
    }


    private void animateView() {
        AnimationSet rollingIn = new AnimationSet(true);
        Animation moving = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -5, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        moving.setDuration(1500);

        Animation rotating = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotating.setDuration(1500);

        rollingIn.addAnimation(rotating);
        rollingIn.addAnimation(moving);
        imageViewNoTracks.setAnimation(rollingIn);

        Animation movingText = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.ZORDER_TOP, -5, Animation.RELATIVE_TO_SELF, 0);
        movingText.setDuration(1000);
        noReminders.setAnimation(movingText);
    }


    private void setScrollingEnabled(boolean isEnabled) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
        if (isEnabled) {
            params.setScrollFlags((AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS));
            collapsingToolbarLayout.setVisibility(View.VISIBLE);
            appBarLayout.setExpanded(isEnabled, isEnabled);
        } else {
            appBarLayout.setExpanded(isEnabled, isEnabled);
            params.setScrollFlags(0);
            collapsingToolbarLayout.setVisibility(View.GONE);
        }
    }


    private void showSnackBar(final int position, final int typeFit, int resTitleId, int resActionId) {
        final int idAlarm = reminderModel.get(position).getId();
        final long dateAlarm = reminderModel.get(position).getDateReminder();
        final int typeAlarm = reminderModel.get(position).getTypeReminder();
        remindersAdapter.deletePositionData(position);

        Snackbar.make(recyclerView, resTitleId, Snackbar.LENGTH_SHORT)
                .setAction(resActionId, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReminderModel reminderModelItem = new ReminderModel(idAlarm, dateAlarm, typeAlarm);
                        remindersAdapter.addData(position, reminderModelItem);
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        switch (event) {
                            case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                cancelAlarm(idAlarm, getTypeFit(typeFit, true, R.array.type_reminder));
                                deleteReminderDb(idAlarm);

                                if (reminderModel.size() == 0) {
                                    showReminders();
                                }
                                break;
                        }
                    }
                }).show();
    }


    private void getRemindersFromDb() {
        reminderModel.clear();
        Cursor cursorReminders = new InquiryBuilder()
                .get(_ID, DATE_REMINDER, TYPE_REMINDER)
                .from(TABLE_REMINDER)
                .orderBy(DATE_REMINDER)
                .select();

        DbCursor dbCursor = new DbCursor(cursorReminders);
        if (dbCursor.isValid()) {
            do {
                reminderModel.add(new ReminderModel(
                        dbCursor.getInt(_ID),
                        dbCursor.getLong(DATE_REMINDER),
                        dbCursor.getInt(TYPE_REMINDER)
                ));
            } while (cursorReminders.moveToNext());
        }
        dbCursor.close();
    }


    private void cancelAlarm(int id, String textReminder) {
        AlarmManager alarmManager = (AlarmManager) App.getInstance().getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                App.getInstance(),
                id,
                new Intent(App.getInstance(), ReminderReceiver.class)
                        .putExtra(EXTRA_ID_REMINDER_KEY, id)
                        .putExtra(EXTRA_TEXT_TYPE_REMINDER_KEY, textReminder),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        alarmManager.cancel(pendingIntent);
    }


    private String getEmojiByUnicode(int unicodeEmoji) {
        return new String(Character.toChars(unicodeEmoji));
    }


    @Override
    public void onResume() {
        super.onResume();
        showReminders();

        LocalBroadcastManager.getInstance(App.getInstance())
                .registerReceiver(broadcastUpdateTime, new IntentFilter(UpdateTimeReminderDialog.BROADCAST_UPDATE_REMINDER_TIME));

        LocalBroadcastManager.getInstance(App.getInstance())
                .registerReceiver(broadcastUpdateDate, new IntentFilter(UpdateDateReminderDialog.BROADCAST_UPDATE_REMINDER_DATE));
    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastUpdateTime);
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastUpdateDate);
    }
}
