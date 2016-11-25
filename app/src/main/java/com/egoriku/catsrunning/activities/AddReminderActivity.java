package com.egoriku.catsrunning.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.ui.CustomStringPicker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.egoriku.catsrunning.helpers.DbActions.writeReminderDb;
import static com.egoriku.catsrunning.models.Constants.ModelReminder.KEY_CALENDAR;
import static com.egoriku.catsrunning.models.Constants.ModelReminder.KEY_CONDITION;
import static com.egoriku.catsrunning.models.Constants.ModelReminder.KEY_TOOLBAR;
import static com.egoriku.catsrunning.models.Constants.ModelReminder.KEY_VISIBLE_BTN_BACK;
import static com.egoriku.catsrunning.models.Constants.ModelReminder.KEY_VISIBLE_DATE_PICKER;
import static com.egoriku.catsrunning.models.Constants.ModelReminder.KEY_VISIBLE_STRING_PICKER;
import static com.egoriku.catsrunning.models.Constants.ModelReminder.KEY_VISIBLE_TEXT_VIEW;
import static com.egoriku.catsrunning.models.Constants.ModelReminder.KEY_VISIBLE_TIME_PICKER;
import static com.egoriku.catsrunning.utils.AlarmsUtility.setAlarm;
import static com.egoriku.catsrunning.utils.TypeFitBuilder.getTypeFit;

public class AddReminderActivity extends AppCompatActivity {
    private static final int UNICODE_EMOJI = 0x1F638;
    private CustomStringPicker stringPicker;
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
        stringPicker = (CustomStringPicker) findViewById(R.id.add_reminder_part_one_string_picker);
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
        listPicker = Arrays.asList(getResources().getStringArray(R.array.type_reminder));
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

        datePicker.init(allDateCalendar.get(Calendar.YEAR), allDateCalendar.get(Calendar.MONTH), allDateCalendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                allDateCalendar.set(year, monthOfYear, dayOfMonth);
            }
        });

        textViewType.setText(textType + " " + getEmojiByUnicode(UNICODE_EMOJI));

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
                    condition = 3;
                    return;
                }

                if (condition == 3) {
                    if (allDateCalendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                        Toast.makeText(App.getInstance(), getString(R.string.reminders_fragment_error_date) + " " + getEmojiByUnicode(UNICODE_EMOJI), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    btnNext.setClickable(false);
                    setAlarm(
                            writeReminderDb(allDateCalendar.getTimeInMillis() / 1000, stringPicker.getCurrent() + 1),
                            getTypeFit(stringPicker.getCurrent() + 1, false, R.array.type_reminder),
                            allDateCalendar.getTimeInMillis(),
                            stringPicker.getCurrent() + 1
                    );
                    finish();
                    return;
                }
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (condition == 2) {
                    textViewType.setVisibility(View.VISIBLE);
                    stringPicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.GONE);
                    btnBack.setVisibility(View.GONE);
                    condition = 1;
                    return;
                }

                if (condition == 3) {
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
