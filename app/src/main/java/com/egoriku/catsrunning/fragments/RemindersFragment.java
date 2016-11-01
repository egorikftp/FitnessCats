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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.AddReminderActivity;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.egoriku.catsrunning.adapters.RemindersAdapter;
import com.egoriku.catsrunning.adapters.interfaces.IRemindersClickListener;
import com.egoriku.catsrunning.fragments.dialogs.UpdateCommentDialogFragment;
import com.egoriku.catsrunning.fragments.dialogs.UpdateDateDialogFragment;
import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.models.ReminderModel;
import com.egoriku.catsrunning.receivers.ReminderReceiver;

import java.util.ArrayList;

import static com.egoriku.catsrunning.models.State.DATE_REMINDER;
import static com.egoriku.catsrunning.models.State.TABLE_REMINDER;
import static com.egoriku.catsrunning.models.State.TEXT_REMINDER;
import static com.egoriku.catsrunning.models.State._ID;
import static com.egoriku.catsrunning.models.State._ID_EQ;

public class RemindersFragment extends Fragment {
    public static final String TAG_REMINDERS_FRAGMENT = "TAG_REMINDERS_FRAGMENT";
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_TYPE_REMINDER = "KEY_TYPE_REMINDER";
    public static final String KEY_UPDATE_REMINDER = "KEY_UPDATE_REMINDER";
    private static final int UNICODE_EMOJI = 0x1F61E;

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private AppBarLayout appBarLayout;
    private ImageView imageViewNoTracks;
    private TextView noReminders;

    private ArrayList<ReminderModel> reminderModels;
    private RemindersAdapter remindersAdapter;

    private BroadcastReceiver broadcastAddReminder = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reminderModels.clear();
            showReminders();
        }
    };
    private BroadcastReceiver broadcastCommentUpdateReminder = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reminderModels.clear();
            showReminders();
        }
    };
    private BroadcastReceiver broadcastDateUpdateReminder = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reminderModels.clear();
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
        reminderModels = new ArrayList<>();
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

        if (reminderModels.size() == 0) {
            appBarLayout.setExpanded(false);
            imageViewNoTracks.setVisibility(View.VISIBLE);
            noReminders.setText(String.format((String) getResources().getText(R.string.reminders_fragment_no_more_reminders), getEmojiByUnicode(UNICODE_EMOJI)));
        } else {
            appBarLayout.setExpanded(true);
            imageViewNoTracks.setVisibility(View.GONE);
            remindersAdapter.setData(reminderModels);
            recyclerView.setAdapter(remindersAdapter);

            remindersAdapter.setOnItemClickListener(new IRemindersClickListener() {
                @Override
                public void onDeleteReminderClick(int id, String textReminder, int position) {
                    cancelAlarm(id, textReminder);
                    updateDb(id);

                    reminderModels.remove(position);
                    remindersAdapter.notifyItemRemoved(position);
                    remindersAdapter.notifyItemRangeChanged(position, reminderModels.size());

                    if (reminderModels.size() == 0) {
                        Snackbar.make(recyclerView, R.string.reminders_fragment_snackbar_reminders_empty, Snackbar.LENGTH_LONG)
                                .setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        switch (event) {
                                            case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                                showReminders();
                                                break;
                                        }
                                    }
                                }).show();
                    }
                }

                @Override
                public void onCommentReminderClick(int id, long dateReminder, String textReminder, int position) {
                    UpdateCommentDialogFragment.newInstance(id, textReminder, dateReminder).show(getFragmentManager(), null);
                }

                @Override
                public void onDateReminderClick(int id, long dateReminder, String textReminder, int position) {
                    UpdateDateDialogFragment.newInstance(id, textReminder, dateReminder).show(getFragmentManager(), null);
                }
            });
        }
    }


    private void getRemindersFromDb() {
        Cursor cursorReminders = new InquiryBuilder()
                .get(_ID, DATE_REMINDER, TEXT_REMINDER)
                .from(TABLE_REMINDER)
                .orderBy(TEXT_REMINDER)
                .desc()
                .select();

        DbCursor dbCursor = new DbCursor(cursorReminders);
        if (dbCursor.isValid()) {
            do {
                reminderModels.add(new ReminderModel(
                        dbCursor.getInt(_ID),
                        dbCursor.getLong(DATE_REMINDER),
                        dbCursor.getString(TEXT_REMINDER)
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
                        .putExtra(ReminderReceiver.ID_KEY, id)
                        .putExtra(ReminderReceiver.TEXT_REMINDER_KEY, textReminder),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        alarmManager.cancel(pendingIntent);
    }


    private void updateDb(int idReminder) {
        new InquiryBuilder()
                .tableDelete(TABLE_REMINDER)
                .where(false, _ID_EQ, String.valueOf(idReminder))
                .delete();
    }


    private String getEmojiByUnicode(int unicodeEmoji) {
        return new String(Character.toChars(unicodeEmoji));
    }


    @Override
    public void onResume() {
        super.onResume();
        showReminders();

        LocalBroadcastManager.getInstance(App.getInstance())
                .registerReceiver(
                        broadcastAddReminder,
                        new IntentFilter(AddReminderActivity.BROADCAST_ADD_NEW_REMINDER)
                );

        LocalBroadcastManager.getInstance(App.getInstance())
                .registerReceiver(
                        broadcastCommentUpdateReminder,
                        new IntentFilter(UpdateCommentDialogFragment.BROADCAST_UPDATE_REMINDER_COMMENT)
                );

        LocalBroadcastManager.getInstance(App.getInstance())
                .registerReceiver(
                        broadcastDateUpdateReminder,
                        new IntentFilter(UpdateDateDialogFragment.BROADCAST_UPDATE_REMINDER_DATE)
                );
    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(App.getInstance())
                .unregisterReceiver(broadcastAddReminder);

        LocalBroadcastManager.getInstance(App.getInstance())
                .unregisterReceiver(broadcastCommentUpdateReminder);

        LocalBroadcastManager.getInstance(App.getInstance())
                .unregisterReceiver(broadcastDateUpdateReminder);
    }
}
