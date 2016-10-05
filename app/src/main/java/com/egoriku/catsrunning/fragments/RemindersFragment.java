package com.egoriku.catsrunning.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.AddReminderActivity;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.egoriku.catsrunning.adapters.RemindersAdapter;
import com.egoriku.catsrunning.adapters.interfaces.IRecyclerViewRemindersListener;
import com.egoriku.catsrunning.fragments.dialogs.UpdateCommentDialogFragment;
import com.egoriku.catsrunning.fragments.dialogs.UpdateDateDialogFragment;
import com.egoriku.catsrunning.models.ReminderModel;
import com.egoriku.catsrunning.receivers.ReminderReceiver;

import java.util.ArrayList;

public class RemindersFragment extends Fragment {

    private static final int UNICODE_EMOJI = 0x1F61E;
    public static final String TAG_REMINDERS_FRAGMENT = "TAG_REMINDERS_FRAGMENT";
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_TYPE_REMINDER = "KEY_TYPE_REMINDER";
    public static final String KEY_UPDATE_REMINDER = "KEY_UPDATE_REMINDER";

    private RecyclerView recyclerViewReminders;
    private FloatingActionButton floatingActionButton;
    private TextView noReminders;

    private ArrayList<ReminderModel> reminderModels;
    private RemindersAdapter remindersAdapter;


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
        recyclerViewReminders = (RecyclerView) view.findViewById(R.id.reminders_recycler_view);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.reminders_fab_add);
        noReminders = (TextView) view.findViewById(R.id.reminders_fragment_no_more_reminders);

        showReminders();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddReminderActivity.class));
            }
        });
        return view;
    }


    private void showReminders() {
        noReminders.setText(null);

        Cursor cursor = App.getInstance().getDb().rawQuery("SELECT Reminder._id AS id, Reminder.dateReminder AS date, Reminder.textReminder AS text FROM Reminder ORDER BY textReminder DESC", null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {
                    ReminderModel reminderModel = new ReminderModel();
                    reminderModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    reminderModel.setDateReminder(cursor.getInt(cursor.getColumnIndexOrThrow("date")));
                    reminderModel.setTextReminder(cursor.getString(cursor.getColumnIndexOrThrow("text")));

                    reminderModels.add(reminderModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (reminderModels.size() == 0) {
            noReminders.setText(
                    String.format(
                            "%s%s",
                            getResources().getText(R.string.reminders_fragment_no_more_reminders),
                            getEmojiByUnicode(UNICODE_EMOJI))
            );
        } else {
            remindersAdapter = new RemindersAdapter(reminderModels);
            recyclerViewReminders.setLayoutManager(new LinearLayoutManager(App.getInstance()));
            recyclerViewReminders.setAdapter(remindersAdapter);
            recyclerViewReminders.hasFixedSize();

            remindersAdapter.setOnItemClickListener(new IRecyclerViewRemindersListener() {
                @Override
                public void onDeleteReminderClick(int position) {
                    cancelAlarm(
                            reminderModels.get(position).getId(),
                            reminderModels.get(position).getTextReminder()
                    );

                    updateDb(reminderModels.get(position).getId());

                    reminderModels.remove(position);
                    remindersAdapter.notifyItemRemoved(position);
                    remindersAdapter.notifyItemRangeChanged(position, reminderModels.size());

                    if (reminderModels.size() == 0) {
                        Snackbar.make(recyclerViewReminders, R.string.reminders_fragment_snackbar_reminders_empty, Snackbar.LENGTH_LONG).show();
                    }
                }

                public void onCommentReminderClick(int position) {
                    UpdateCommentDialogFragment.newInstance(
                            reminderModels.get(position).getId(),
                            reminderModels.get(position).getTextReminder(),
                            reminderModels.get(position).getDateReminder()
                    ).show(getFragmentManager(), null);
                }

                @Override
                public void onDateReminderClick(int position) {
                    UpdateDateDialogFragment.newInstance(
                            reminderModels.get(position).getId(),
                            reminderModels.get(position).getTextReminder(),
                            reminderModels.get(position).getDateReminder()
                    ).show(getFragmentManager(), null);
                }
            });
        }
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
        SQLiteStatement statement = App.getInstance().getDb().compileStatement("DELETE FROM Reminder WHERE _id = ?");

        statement.bindLong(1, idReminder);
        try {
            statement.execute();
        } finally {
            statement.close();
        }
    }


    private String getEmojiByUnicode(int unicodeEmoji) {
        return new String(Character.toChars(unicodeEmoji));
    }


    @Override
    public void onResume() {
        super.onResume();

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
}
