package com.egoriku.catsrunning.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.models.Firebase.Point;
import com.egoriku.catsrunning.models.Firebase.SaveModel;
import com.egoriku.catsrunning.services.RunService;
import com.egoriku.catsrunning.utils.CustomChronometer;
import com.egoriku.catsrunning.utils.FlipAnimation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static com.egoriku.catsrunning.helpers.DbActions.deleteTrackData;
import static com.egoriku.catsrunning.models.State.BEGINS_AT;
import static com.egoriku.catsrunning.models.State.DISTANCE;
import static com.egoriku.catsrunning.models.State.LAT;
import static com.egoriku.catsrunning.models.State.LNG;
import static com.egoriku.catsrunning.models.State.TABLE_POINT;
import static com.egoriku.catsrunning.models.State.TABLE_TRACKS;
import static com.egoriku.catsrunning.models.State.TIME;
import static com.egoriku.catsrunning.models.State.TRACK_ID_EQ;
import static com.egoriku.catsrunning.models.State.TRACK_TOKEN;
import static com.egoriku.catsrunning.models.State.TYPE_FIT;
import static com.egoriku.catsrunning.models.State._ID;
import static com.egoriku.catsrunning.models.State._ID_EQ;
import static com.egoriku.catsrunning.utils.TypeFitBuilder.getTypeFit;

public class ScamperActivity extends AppCompatActivity {
    public static final String BROADCAST_FINISH_SERVICE = "BROADCAST_FINISH_SERVICE";
    public static final String KEY_TYPE_FIT = "KEY_TYPE_FIT";
    public static final String KEY_TYPE_FIT_NOTIFICATION = "KEY_TYPE_FIT_NOTIFICATION";
    private static final int REQUEST_CODE = 1;
    private static final String VIEW_BTN_START = "VIEW_BTN_START";
    private static final String VIEW_BTN_FINISH = "VIEW_BTN_FINISH";
    private static final String VIEW_TEXT_TIMER = "VIEW_TEXT_TIMER";
    private static final String VIEW_TEXT_DISTANCE = "VIEW_TEXT_DISTANCE";
    private static final String VIEW_TEXT_FINISH_TEXT = "VIEW_TEXT_FINISH_TEXT";
    private static final String VIEW_IMAGE = "VIEW_IMAGE";
    private static final String DISTANCE_TEXT = "DISTANCE_TEXT";
    private static final String TIME_SCAMPER_TEXT = "TIME_SCAMPER_TEXT";
    private static final String KEY_IS_CHRONOMETER_RUNNING = "KEY_IS_CHRONOMETER_RUNNING";
    private static final String KEY_START_TIME = "KEY_START_TIME";
    private static final String TOOLBAR_TEXT = "TOOLBAR_TEXT";
    private static final String EXTRA_ID_TRACK = "EXTRA_ID_TRACK";
    private ArrayList<Point> points;

    private int idTrack;
    private int typeFit;
    private String alertMessage;
    private String alertPositiveBtn;
    private String alertNegativeBtn;
    private String textNowTime;

    private Toolbar toolbar;
    private Button btnStart;
    private Button btnFinish;
    private TextView textTimer;
    private TextView textDistance;
    private ImageView pandaFinishScamper;
    private LinearLayout linearLayoutRoot;

    private CustomChronometer chronometer;
    private LocationManager manager;
    private Thread chronometerThread;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scamper);
        user = FirebaseAuth.getInstance().getCurrentUser();
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        btnStart = (Button) findViewById(R.id.scamper_activity_btn_start);
        btnFinish = (Button) findViewById(R.id.scamper_activity_btn_finish);
        textTimer = (TextView) findViewById(R.id.scamper_activity_text_timer);
        textDistance = (TextView) findViewById(R.id.scamper_activity_text_distance);
        pandaFinishScamper = (ImageView) findViewById(R.id.image_panda_finish_scamper);
        linearLayoutRoot = (LinearLayout) findViewById(R.id.activity_scamper_root_layout);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        alertMessage = getString(R.string.scamper_activity_alert_message_no_gps);
        alertPositiveBtn = getString(R.string.scamper_activity_alert_positive_btn);
        alertNegativeBtn = getString(R.string.scamper_activity_alert_negative_btn);
        textNowTime = getString(R.string.scamper_activity_now_time);

        btnFinish.setVisibility(View.GONE);
        textTimer.setVisibility(View.GONE);
        textDistance.setVisibility(View.GONE);
        pandaFinishScamper.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (getIntent().getExtras() != null) {
                typeFit = getIntent().getExtras().getInt(KEY_TYPE_FIT);
                getSupportActionBar().setTitle(getTypeFit(typeFit, false, R.array.type_reminder));
            }
        }

        final FlipAnimation flipAnimation = new FlipAnimation(btnStart, btnFinish, textTimer, textDistance, pandaFinishScamper);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(ScamperActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ScamperActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_CODE
                        );
                        return;
                    }
                }

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                    return;
                }

                if (chronometer == null) {
                    chronometer = new CustomChronometer(ScamperActivity.this);
                    chronometerThread = new Thread(chronometer);
                    chronometerThread.start();
                    chronometer.startChronometer();
                }

                Intent intent = new Intent(ScamperActivity.this, RunService.class);
                intent.putExtra(RunService.START_TIME, chronometer.getStartTime());
                if (getIntent().getExtras() != null) {
                    intent.putExtra(KEY_TYPE_FIT_NOTIFICATION, getIntent().getExtras().getInt(KEY_TYPE_FIT));
                }

                intent.setAction(RunService.ACTION_START);
                startService(intent);

                linearLayoutRoot.startAnimation(flipAnimation);
            }
        });


        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipAnimation.setReverse();
                linearLayoutRoot.startAnimation(flipAnimation);

                if (chronometer != null) {
                    chronometer.stopChronometer();
                    chronometerThread.interrupt();
                    chronometerThread = null;
                    chronometer = null;
                }

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(
                            String.format(getString(R.string.scamper_activity_toolbar_title),getTypeFit(typeFit, true, R.array.all_fitness_data_categories))
                    );
                }

                stopService(new Intent(ScamperActivity.this, RunService.class));
            }
        });
    }


    public void updateTimer(final String timeFromChronometer) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textTimer.setText(String.format(textNowTime,timeFromChronometer));
            }
        });
    }


    private void buildAlertMessageNoGps() {
        new AlertDialog.Builder(this)
                .setMessage(alertMessage)
                .setCancelable(false)
                .setPositiveButton(alertPositiveBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(alertNegativeBtn, null)
                .create()
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btnStart.callOnClick();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if (btnFinish.getVisibility() == View.GONE && (btnStart.getVisibility() == View.GONE || btnStart.getVisibility() == View.VISIBLE)) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), R.string.scamper_activity_snackbar_btn_finish_not_pressed, Toast.LENGTH_SHORT).show();
            return;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveInstance();
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastReceiverIdTrack);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadInstance();
        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(broadcastReceiverIdTrack, new IntentFilter(BROADCAST_FINISH_SERVICE));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(VIEW_BTN_START, btnStart.getVisibility());
        outState.putInt(VIEW_BTN_FINISH, btnFinish.getVisibility());
        outState.putInt(VIEW_TEXT_TIMER, textTimer.getVisibility());
        outState.putInt(VIEW_TEXT_DISTANCE, textDistance.getVisibility());
        outState.putString(TOOLBAR_TEXT, toolbar.getTitle().toString());
        outState.putString(DISTANCE_TEXT, textDistance.getText().toString());
        outState.putString(TIME_SCAMPER_TEXT, textTimer.getText().toString());
        outState.putInt(VIEW_IMAGE, pandaFinishScamper.getVisibility());
    }


    @SuppressWarnings("WrongConstant")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        btnStart.setVisibility(savedInstanceState.getInt(VIEW_BTN_START));
        btnFinish.setVisibility(savedInstanceState.getInt(VIEW_BTN_FINISH));
        textTimer.setVisibility(savedInstanceState.getInt(VIEW_TEXT_TIMER));
        textDistance.setVisibility(savedInstanceState.getInt(VIEW_TEXT_DISTANCE));
        pandaFinishScamper.setVisibility(savedInstanceState.getInt(VIEW_IMAGE));
        textDistance.setText(savedInstanceState.getString(DISTANCE_TEXT));
        textTimer.setText(savedInstanceState.getString(TIME_SCAMPER_TEXT));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(savedInstanceState.getString(TOOLBAR_TEXT));
        }
    }


    private void saveInstance() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        if (chronometer != null && chronometer.isRunning()) {
            editor.putBoolean(KEY_IS_CHRONOMETER_RUNNING, chronometer.isRunning());
            editor.putLong(KEY_START_TIME, chronometer.getStartTime());
        } else {
            editor.putBoolean(KEY_IS_CHRONOMETER_RUNNING, false);
            editor.putLong(KEY_START_TIME, 0);
        }
        editor.apply();
    }


    private void loadInstance() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long lastStartTime = preferences.getLong(KEY_START_TIME, 0);

        if (preferences.getBoolean(KEY_IS_CHRONOMETER_RUNNING, false) && lastStartTime != 0 && chronometer == null) {

            if (chronometerThread != null) {
                chronometerThread.interrupt();
                chronometerThread = null;
            }

            chronometer = new CustomChronometer(this, lastStartTime);
            chronometerThread = new Thread(chronometer);
            chronometerThread.start();
            chronometer.startChronometer();

            btnStart.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
            textTimer.setVisibility(View.VISIBLE);
            textDistance.setVisibility(View.GONE);
        }
    }


    private void writeKeyToDb(String key, int idTrack) {
        new InquiryBuilder()
                .updateTable(TABLE_TRACKS)
                .set(TRACK_TOKEN, key)
                .updateWhere(_ID_EQ, String.valueOf(idTrack))
                .update();
    }


    private BroadcastReceiver broadcastReceiverIdTrack = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            idTrack = intent.getIntExtra(EXTRA_ID_TRACK, -1);
            textDistance.setText(String.format(getString(R.string.scamper_activity_distance_meter), App.getInstance().getState().getNowDistance()));

            points = new ArrayList<>();
            long beginsAt = 0;
            int distance = 0;
            long time = 0;
            int typeFit = 0;

            Cursor cursorData = new InquiryBuilder()
                    .get(BEGINS_AT, TIME, DISTANCE, TYPE_FIT)
                    .from(TABLE_TRACKS)
                    .where(false, _ID_EQ, String.valueOf(idTrack))
                    .select();

            DbCursor dbCursor = new DbCursor(cursorData);

            if (dbCursor.isValid()) {
                do {
                    beginsAt = dbCursor.getInt(BEGINS_AT);
                    distance = dbCursor.getInt(DISTANCE);
                    time = dbCursor.getInt(TIME);
                    typeFit = dbCursor.getInt(TYPE_FIT);
                } while (cursorData.moveToNext());
            }
            dbCursor.close();

            Cursor cursorPoints = new InquiryBuilder()
                    .get(_ID, LNG, LAT)
                    .from(TABLE_POINT)
                    .where(false, TRACK_ID_EQ, String.valueOf(idTrack))
                    .select();

            DbCursor dbCursorPoints = new DbCursor(cursorPoints);
            if (dbCursorPoints.isValid()) {
                do {
                    Point point = new Point();
                    point.setLat(dbCursorPoints.getDouble(LAT));
                    point.setLng(dbCursorPoints.getDouble(LNG));
                    points.add(point);
                } while (cursorPoints.moveToNext());
            }
            dbCursor.close();

            if (points.size() > 1) {
                String trackToken = App.getInstance().getTracksReference().child(user.getUid()).push().getKey();
                writeKeyToDb(trackToken, idTrack);
                SaveModel saveModel = new SaveModel(beginsAt, time, distance, trackToken, typeFit, points);

                App.getInstance().getTracksReference().child(user.getUid()).child(trackToken).setValue(saveModel, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Snackbar.make(linearLayoutRoot, getString(R.string.scamper_activity_track_save_error) + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(linearLayoutRoot, R.string.scamper_activity_track_save_success, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), R.string.scamper_activity_snackbar_no_points, Toast.LENGTH_SHORT).show();
                deleteTrackData(idTrack);
            }
        }
    };
}
