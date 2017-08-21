package com.egoriku.catsrunning.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.data.commons.TracksModel;
import com.egoriku.catsrunning.helpers.TypeFit;
import com.egoriku.catsrunning.models.Constants;
import com.egoriku.catsrunning.models.FitState;
import com.egoriku.catsrunning.models.ParcelableFitActivityModel;
import com.egoriku.catsrunning.services.FitService;
import com.egoriku.catsrunning.utils.CustomChronometer;
import com.egoriku.catsrunning.utils.FirebaseUtils;
import com.egoriku.catsrunning.utils.FlipAnimation;
import com.egoriku.catsrunning.utils.IntentBuilder;
import com.egoriku.catsrunning.utils.TimeUtil;

import static com.egoriku.catsrunning.models.Constants.Extras.KEY_TYPE_FIT;
import static com.egoriku.catsrunning.models.Constants.ModelScamperActivity.KEY_IS_CHRONOMETER_RUNNING;
import static com.egoriku.catsrunning.models.Constants.ModelScamperActivity.KEY_START_TIME;
import static com.egoriku.catsrunning.models.Constants.ModelScamperActivity.PARCELABLE_FIT_ACTIVITY_KEY;
import static com.egoriku.catsrunning.models.Constants.RunService.ACTION_START;
import static com.egoriku.catsrunning.models.Constants.RunService.START_TIME;
import static com.egoriku.catsrunning.utils.TypeFitBuilder.getTypeFit;

public class FitActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    public static final int TOP_PADDING = 50;
    public static final int ANOTHER_PADDING = 0;

    @TypeFit
    private int typeFit;

    private Toolbar toolbar;
    private Button btnStart;
    private Button btnFinish;
    private TextView textViewNowDistance;
    private TextView textViewNowTime;
    private TextView textViewFinalTime;
    private TextView textViewFinalDistance;
    private ImageView imageViewFinish;
    private RelativeLayout relativeRootLayout;

    private CustomChronometer chronometer;
    private LocationManager manager;
    private Thread chronometerThread;

    private FitState fitState = FitState.getInstance();
    private FirebaseUtils firebaseUtils = FirebaseUtils.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scamper);

        toolbar = findViewById(R.id.toolbar_app);
        btnStart = findViewById(R.id.fit_activity_btn_start);
        btnFinish = findViewById(R.id.fit_activity_btn_finish);
        textViewFinalTime = findViewById(R.id.fit_activity_final_time);
        textViewFinalDistance = findViewById(R.id.fit_activity_final_distance);
        textViewNowTime = findViewById(R.id.fit_activity_now_time);
        textViewNowDistance = findViewById(R.id.fit_activity_now_distance);
        imageViewFinish = findViewById(R.id.fit_activity_image_finish);
        relativeRootLayout = findViewById(R.id.fit_activity_root_layout);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnFinish.setVisibility(View.GONE);
        textViewNowDistance.setVisibility(View.GONE);
        textViewNowTime.setVisibility(View.GONE);
        imageViewFinish.setVisibility(View.GONE);
        textViewFinalDistance.setVisibility(View.GONE);
        textViewFinalTime.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras() != null) {
            typeFit = getIntent().getExtras().getInt(KEY_TYPE_FIT);
            getSupportActionBar().setTitle(getTypeFit(typeFit, false, R.array.type_reminder));
        }

        final FlipAnimation flipAnimation = new FlipAnimation(btnStart, btnFinish, textViewNowTime, textViewNowDistance, imageViewFinish, textViewFinalTime, textViewFinalDistance);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(FitActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(FitActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                fitState.setFitRun(true);
                fitState.setWeight(Constants.UserInfoSharedPreference.DEFAULT_WEIGHT);

                if (chronometer == null) {
                    chronometer = new CustomChronometer(FitActivity.this);
                    chronometerThread = new Thread(chronometer);
                    chronometerThread.start();
                    chronometer.startChronometer();
                }

                if (getIntent().getExtras() != null) {
                    fitState.setTypeFit(getIntent().getExtras().getInt(KEY_TYPE_FIT));
                }

                IntentBuilder intent = new IntentBuilder()
                        .context(FitActivity.this)
                        .service(FitService.class)
                        .action(ACTION_START)
                        .extra(START_TIME, chronometer.getStartTime());

                startService(intent.build());
                textViewNowTime.setPadding(ANOTHER_PADDING, TOP_PADDING, ANOTHER_PADDING, ANOTHER_PADDING);
                relativeRootLayout.startAnimation(flipAnimation);
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onClick(View view) {
                stopService(new IntentBuilder()
                        .context(FitActivity.this)
                        .service(FitService.class)
                        .build());

                textViewFinalTime.setText(String.format(getString(R.string.fit_activity_now_time), TimeUtil.ConvertTimeToString(fitState.getSinceTime())));
                textViewFinalDistance.setText(String.format(getString(R.string.fit_activity_final_distance_meter), (int) fitState.getNowDistance()));
                flipAnimation.setReverse();
                relativeRootLayout.startAnimation(flipAnimation);

                if (chronometer != null) {
                    chronometer.stopChronometer();
                    chronometerThread.interrupt();
                    chronometerThread = null;
                    chronometer = null;
                }

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(String.format(getString(R.string.scamper_activity_toolbar_title), getTypeFit(typeFit, true, R.array.all_fitness_data_categories)));
                }

                uploadTrackInFirebase();
            }
        });
    }

    private void uploadTrackInFirebase() {
        if (fitState.getLatLngs().size() > 2) {

            TracksModel tracksModel = new TracksModel(
                    fitState.getStartTime() / 1000L,
                    fitState.getSinceTime(),
                    (int) fitState.getNowDistance(),
                    firebaseUtils.getTrackToken(),
                    typeFit,
                    fitState.getLatLngs());

            firebaseUtils.saveFit(tracksModel, relativeRootLayout);
        } else {
            Snackbar.make(relativeRootLayout, R.string.fit_activity_snackbar_low_points, Snackbar.LENGTH_LONG).show();
        }

        fitState.clearFitData();
    }

    public void updateTimer(final String timeFromChronometer) {
        runOnUiThread(new Runnable() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void run() {
                textViewNowTime.setText(String.format(getString(R.string.fit_activity_now_time), timeFromChronometer));
                textViewNowDistance.setText(String.format(getString(R.string.fit_activity_now_distance_meter), (int) fitState.getNowDistance()));
            }
        });
    }

    private void buildAlertMessageNoGps() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.fit_activity_alert_message_no_gps)
                .setCancelable(false)
                .setPositiveButton(R.string.fit_activity_alert_positive_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new IntentBuilder()
                                .action(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                .build());
                    }
                })
                .setNegativeButton(R.string.fit_activity_alert_negative_btn, null)
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStance();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ParcelableFitActivityModel model = new ParcelableFitActivityModel();
        model.setBtnStart(btnStart.getVisibility());
        model.setBtnFinish(btnFinish.getVisibility());
        model.setTextTimeFitVisibility(textViewNowTime.getVisibility());
        model.setTextDistanceVisibility(textViewNowDistance.getVisibility());
        model.setImageViewFinish(imageViewFinish.getVisibility());
        model.setToolbarText(toolbar.getTitle().toString());
        model.setTextDistance(textViewNowDistance.getText().toString());
        model.setTextTimeFit(textViewNowTime.getText().toString());
        model.setTextViewFinalDistanceTxt(textViewFinalDistance.getText().toString());
        model.setTextViewFinalDistance(textViewFinalDistance.getVisibility());
        model.setTextViewFinalTime(textViewFinalTime.getVisibility());
        model.setTextViewFinalTimeTxt(textViewFinalTime.getText().toString());
        outState.putParcelable(PARCELABLE_FIT_ACTIVITY_KEY, model);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ParcelableFitActivityModel model = savedInstanceState.getParcelable(PARCELABLE_FIT_ACTIVITY_KEY);

        if (model != null) {
            btnStart.setVisibility(model.getBtnStart());
            btnFinish.setVisibility(model.getBtnFinish());
            textViewNowDistance.setVisibility(model.getTextDistanceVisibility());
            imageViewFinish.setVisibility(model.getImageViewFinish());
            textViewNowTime.setVisibility(model.getTextTimeFitVisibility());
            textViewNowDistance.setText(model.getTextDistance());
            textViewNowTime.setText(model.getTextTimeFit());
            textViewFinalTime.setVisibility(model.getTextViewFinalTime());
            textViewFinalTime.setText(model.getTextViewFinalTimeTxt());
            textViewFinalDistance.setVisibility(model.getTextViewFinalDistance());
            textViewFinalDistance.setText(model.getTextViewFinalDistanceTxt());

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(model.getToolbarText());
            }
        }
    }

    private void saveState() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        if (chronometer != null && chronometer.isRunning()) {
            editor.putBoolean(KEY_IS_CHRONOMETER_RUNNING, chronometer.isRunning());
            editor.putLong(KEY_START_TIME, chronometer.getStartTime());
        } else {
            editor.putBoolean(KEY_IS_CHRONOMETER_RUNNING, false);
            editor.putLong(KEY_START_TIME, 0);
        }
        editor.apply();
    }

    private void loadStance() {
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
            textViewNowDistance.setVisibility(View.VISIBLE);
            textViewNowTime.setVisibility(View.VISIBLE);
            textViewNowTime.setPadding(ANOTHER_PADDING, TOP_PADDING, ANOTHER_PADDING, ANOTHER_PADDING);
        }
    }

    public static void start(Context context, @TypeFit int typeFit) {
        Intent intent = new Intent(context, FitActivity.class);
        intent.putExtra(KEY_TYPE_FIT, typeFit);
        context.startActivity(intent);
    }
}
