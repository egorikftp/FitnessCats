package com.egoriku.catsrunning.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.FitActivity;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.utils.ConverterTime;

import static com.egoriku.catsrunning.models.Constants.Broadcast.BROADCAST_FINISH_SERVICE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.BEGINS_AT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.DISTANCE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LAT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LNG;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TIME;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TRACK_ID;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TYPE_FIT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query._ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_POINT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;
import static com.egoriku.catsrunning.models.Constants.Extras.KEY_TYPE_FIT;
import static com.egoriku.catsrunning.models.Constants.KeyNotification.KEY_TYPE_FIT_NOTIFICATION;
import static com.egoriku.catsrunning.models.Constants.ModelScamperActivity.EXTRA_ID_TRACK;
import static com.egoriku.catsrunning.models.Constants.RunService.ACTION_START;
import static com.egoriku.catsrunning.models.Constants.RunService.START_TIME;
import static com.egoriku.catsrunning.utils.TypeFitBuilder.getTypeFit;

public class RunService extends Service implements LocationListener {
    private static final int NOTIFICATION_ID = 1;
    private static final long TIME_BETWEEN_UPDATES = 1000;
    private static final float UPDATE_DISTANCE_THRESHOLD_METERS = 5.0f;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private boolean isActive;
    private boolean isThreadRun;
    private int typeFit;

    private long startTime;
    private float nowDistance;
    private long idTrack;

    private UpdateNotification updateNotification;
    private Thread updateThread;

    private LocationManager locationManager;
    private Location oldLocation;

    private int[] imageResId = {
            R.drawable.ic_directions_walk_black_service,
            R.drawable.ic_directions_run_black_service,
            R.drawable.ic_directions_bike_black_service
    };


    @Override
    public void onCreate() {
        super.onCreate();
        isActive = false;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equalsIgnoreCase(ACTION_START)) {
            startTime = intent.getLongExtra(START_TIME, System.currentTimeMillis());
            typeFit = intent.getIntExtra(KEY_TYPE_FIT_NOTIFICATION, 0);
            startNotification();
        } else {
            stopNotification();
            stopSelf();
        }

        if (!isActive) {
            isActive = true;
            getTrackIdStatement(startTime, typeFit);

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    TIME_BETWEEN_UPDATES,
                    UPDATE_DISTANCE_THRESHOLD_METERS,
                    this
            );
        }
        return START_STICKY;
    }


    private void getTrackIdStatement(long startTime, int typeFit) {
        idTrack = new InquiryBuilder()
                .table(TABLE_TRACKS)
                .set(BEGINS_AT, startTime / 1000)
                .set(TYPE_FIT, typeFit)
                .insertForId(App.getInstance().getDb());
    }


    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, oldLocation)) {
            if (oldLocation != null) {
                float[] results = new float[1];
                Location.distanceBetween(
                        oldLocation.getLatitude(), oldLocation.getLongitude(),
                        location.getLatitude(), location.getLongitude(),
                        results
                );
                nowDistance = nowDistance + results[0];
                App.getInstance().getState().setNowDistance((int) nowDistance);
            }

            oldLocation = location;
            insertLocationToDb(location.getLongitude(), location.getLatitude());
        }
    }


    private void insertLocationToDb(double longitude, double latitude) {
        new InquiryBuilder()
                .table(TABLE_POINT)
                .set(LAT, latitude)
                .set(LNG, longitude)
                .set(TRACK_ID, idTrack)
                .insert(App.getInstance().getDb());
    }


    @Override
    public void onDestroy() {
        if (isActive) {
            isActive = false;
            locationManager.removeUpdates(this);
            stopForeground(true);
            insertTrackData(nowDistance, App.getInstance().getState().getSinceTime());

            LocalBroadcastManager.getInstance(
                    App.getInstance()).sendBroadcast(new Intent(BROADCAST_FINISH_SERVICE).putExtra(EXTRA_ID_TRACK, idTrack));
        }
        super.onDestroy();
    }


    private void insertTrackData(float nowDistance, long sinceTime) {
        new InquiryBuilder()
                .updateTable(TABLE_TRACKS)
                .set(DISTANCE, nowDistance)
                .set(TIME, sinceTime)
                .updateWhere(_ID_EQ, String.valueOf(idTrack))
                .update();
    }


    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    private boolean isSameProvider(String provider, String providerBestLocation) {
        if (provider == null) {
            return providerBestLocation == null;
        }
        return provider.equals(providerBestLocation);
    }


    private void startNotification() {
        isThreadRun = true;

        if (updateNotification == null) {
            updateNotification = new UpdateNotification();
            updateThread = new Thread(updateNotification);
            updateThread.start();
        } else {
            stopNotification();
            startNotification();
        }
    }


    private void showNotification(String time, String distance) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon(typeFit))
                .setContentIntent(PendingIntent.getActivity(
                        this,
                        0,
                        new Intent(this, FitActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra(KEY_TYPE_FIT, typeFit),
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .setContentTitle(String.format(getString(R.string.scamper_notification_title), getTypeFit(typeFit, true, R.array.type_reminder)))
                .setContentText(String.format(getString(R.string.notification_time_distance_format), time, distance))
                .setAutoCancel(false)
                .setOngoing(true);
        startForeground(NOTIFICATION_ID, builder.build());
    }


    private int getNotificationIcon(int typeFit) {
        return imageResId[typeFit - 1];
    }


    private void stopNotification() {
        isThreadRun = false;

        if (updateThread != null) {
            updateThread.interrupt();
        }

        updateThread = null;
        updateNotification = null;
    }


    public String getNowDistance() {
        return String.valueOf((int) nowDistance);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }


    @Override
    public void onProviderEnabled(String s) {
    }


    @Override
    public void onProviderDisabled(String s) {
    }


    class UpdateNotification implements Runnable {
        @Override
        public void run() {
            while (isThreadRun) {
                long since = System.currentTimeMillis() - startTime;

                showNotification(ConverterTime.ConvertTimeToString(since), getNowDistance());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
