package com.egoriku.catsrunning.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.receivers.ReminderReceiver;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import hotchemi.stringpicker.StringPicker;

public class AddReminderActivity extends AppCompatActivity {
    private static final int UNICODE_EMOJI = 0x1F638;
    public static final String BROADCAST_ADD_NEW_REMINDER = "BROADCAST_ADD_NEW_REMINDER";
    private static final String KEY_VISIBLE_STRING_PICKER = "KEY_VISIBLE_STRING_PICKER";
    private static final String KEY_VISIBLE_DATE_PICKER = "KEY_VISIBLE_DATE_PICKER";
    private static final String KEY_VISIBLE_TIME_PICKER = "KEY_VISIBLE_TIME_PICKER";
    private static final String KEY_VISIBLE_BTN_BACK = "KEY_VISIBLE_BTN_BACK";
    private static final String KEY_CONDITION = "KEY_CONDITION";
    private static final String KEY_CALENDAR = "KEY_CALENDAR";
    private static final String KEY_TOOLBAR = "KEY_TOOLBAR";
    private static final String KEY_VISIBLE_TEXT_VIEW = "KEY_VISIBLE_TEXT_VIEW";

    private StringPicker stringPicker;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Toolbar toolbar;

    private List<String> listPicker;
    private Calendar allDateCalendar;
    private Button btnNext;
    private Button btnBack;
    private TextView textViewType;
    private int condition = 1;

    private String textBtnFinish;
    private String textBtnNext;
    private String textToolbar;
    private String textType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_add);

        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        stringPicker = (StringPicker) findViewById(R.id.add_reminder_part_one_string_picker);
        timePicker = (TimePicker) findViewById(R.id.add_reminder_part_two_time_picker);
        datePicker = (DatePicker) findViewById(R.id.add_reminder_part_one_date_picker);
        btnNext = (Button) findViewById(R.id.add_reminder_btn_next);
        btnBack = (Button) findViewById(R.id.add_reminder_btn_back);
        textViewType = (TextView) findViewById(R.id.activity_reminder_type_text);

        textBtnFinish = getString(R.string.add_reminder_part_one_dialog_final_btn);
        textBtnNext = getString(R.string.add_reminder_part_one_dialog_positive_btn);
        textToolbar = getString(R.string.add_reminder_part_one_dialog_title);
        textType = getString(R.string.add_reminder_part_one_dialog_text_type);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(textToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        allDateCalendar = Calendar.getInstance();
        listPicker = Arrays.asList("Пробежка", "Прогулка", "Велопрогулка");

        timePicker.setIs24HourView(true);

        if (Build.VERSION.SDK_INT >= 15 && Build.VERSION.SDK_INT <= 19) {
            datePicker.setCalendarViewShown(false);
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minutes) {
                allDateCalendar.set(Calendar.HOUR_OF_DAY, hour);
                allDateCalendar.set(Calendar.MINUTE, minutes);
            }
        });

        textViewType.setText(textType + " " + getEmojiByUnicode(UNICODE_EMOJI) );

        stringPicker.setVisibility(View.VISIBLE);
        datePicker.setVisibility(View.GONE);
        timePicker.setVisibility(View.GONE);
        stringPicker.setValues(listPicker);
        btnBack.setVisibility(View.GONE);
        textViewType.setVisibility(View.VISIBLE);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (condition == 1) {
                    textViewType.setVisibility(View.GONE);
                    stringPicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    btnBack.setVisibility(View.VISIBLE);
                    condition = 2;
                    return;
                }

                if (condition == 2) {
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);
                    btnNext.setText(textBtnFinish);
                    allDateCalendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                    condition = 3;
                    return;
                }

                if (condition == 3) {
                    setAlarm(
                            writeAlarmDB(allDateCalendar.getTimeInMillis() / 1000, stringPicker.getCurrentValue()),
                            stringPicker.getCurrentValue(),
                            allDateCalendar.getTimeInMillis()
                    );
                    return;
                }
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(condition == 2){
                    textViewType.setVisibility(View.VISIBLE);
                    stringPicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.GONE);
                    btnBack.setVisibility(View.GONE);
                    condition = 1;
                    return;
                }

                if(condition == 3){
                    timePicker.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.GONE);
                    btnBack.setVisibility(View.VISIBLE);
                    btnNext.setText(textBtnNext);
                    condition = 2;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.setHour(allDateCalendar.get(Calendar.HOUR_OF_DAY));
                        timePicker.setMinute(allDateCalendar.get(Calendar.MINUTE));
                    } else {
                        timePicker.setCurrentHour(allDateCalendar.get(Calendar.HOUR_OF_DAY));
                        timePicker.setCurrentMinute(allDateCalendar.get(Calendar.MINUTE));
                    }
                    return;
                }
            }
        });
    }


    private void setAlarm(int id, String textReminder, long timeInMillis) {
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

        LocalBroadcastManager.getInstance(
                App.getInstance()).sendBroadcastSync(new Intent(BROADCAST_ADD_NEW_REMINDER));
        finish();
    }


    private int writeAlarmDB(long dateReminderUnix, String textReminder) {
        int idAlarm;
        SQLiteStatement statement = App.getInstance().getDb().compileStatement(
                "INSERT INTO Reminder (dateReminder, textReminder) VALUES (?, ?)"
        );

        statement.bindLong(1, dateReminderUnix);
        statement.bindString(2, textReminder);

        try {
            idAlarm = (int) statement.executeInsert();
        } finally {
            statement.close();
        }
        return idAlarm;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_VISIBLE_STRING_PICKER, stringPicker.getVisibility());
        outState.putInt(KEY_VISIBLE_DATE_PICKER, datePicker.getVisibility());
        outState.putInt(KEY_VISIBLE_TIME_PICKER, timePicker.getVisibility());
        outState.putInt(KEY_VISIBLE_BTN_BACK, btnBack.getVisibility());
        outState.putInt(KEY_VISIBLE_TEXT_VIEW, textViewType.getVisibility());
        outState.putInt(KEY_CONDITION, condition);
        outState.putLong(KEY_CALENDAR, allDateCalendar.getTimeInMillis());
        outState.putString(KEY_TOOLBAR, toolbar.getTitle().toString());
    }


    @SuppressWarnings("WrongConstant")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        stringPicker.setVisibility(savedInstanceState.getInt(KEY_VISIBLE_STRING_PICKER));
        datePicker.setVisibility(savedInstanceState.getInt(KEY_VISIBLE_DATE_PICKER));
        timePicker.setVisibility(savedInstanceState.getInt(KEY_VISIBLE_TIME_PICKER));
        btnBack.setVisibility(savedInstanceState.getInt(KEY_VISIBLE_BTN_BACK));
        condition = savedInstanceState.getInt(KEY_CONDITION);
        allDateCalendar.setTimeInMillis(savedInstanceState.getLong(KEY_CALENDAR));
        textViewType.setVisibility(savedInstanceState.getInt(KEY_VISIBLE_TEXT_VIEW));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(savedInstanceState.getString(KEY_TOOLBAR));
        }
    }


    private String getEmojiByUnicode(int unicodeEmoji) {
        return new String(Character.toChars(unicodeEmoji));
    }
}
