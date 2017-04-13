package com.egoriku.catsrunning.helpers.dbActions;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.models.AllFitnessDataModel;
import com.egoriku.catsrunning.models.Firebase.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.egoriku.catsrunning.models.Constants.Broadcast.BROADCAST_SAVE_NEW_TRACKS;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.BEGINS_AT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.DISTANCE;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LAT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LNG;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TIME;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TRACK_ID;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TRACK_TOKEN;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.TYPE_FIT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_POINT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_TRACKS;

public class AsyncWriteNewTracks {
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAXIMUM_POOL_SIZE = 4;
    private static final int KEEP_ALIVE = 1;
    private static WriteTaskTrack writeTaskTrack;

    private static final ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(128)
    );


    public static void writeData(AllFitnessDataModel someData, long countTracks) {
        writeTaskTrack = new WriteTaskTrack(someData, countTracks);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            writeTaskTrack.executeOnExecutor(customExecutor);
        } else {
            writeTaskTrack.execute();
        }
    }


    private static class WriteTaskTrack extends AsyncTask<Void, Void, Void> {
        private AllFitnessDataModel someData = new AllFitnessDataModel();
        private long countTracks;

        public WriteTaskTrack(AllFitnessDataModel someData, long countTracks) {
            this.someData = someData;
            this.countTracks = countTracks;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            long idTrack = new InquiryBuilder()
                    .table(TABLE_TRACKS)
                    .set(BEGINS_AT, someData.getBeginsAt())
                    .set(TIME, someData.getTime())
                    .set(DISTANCE, someData.getDistance())
                    .set(TRACK_TOKEN, someData.getTrackToken())
                    .set(TYPE_FIT, someData.getTypeFit())
                    .insertForId(App.getInstance().getDb());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new WriteTaskPoints(someData.getPoints(), idTrack).executeOnExecutor(customExecutor);
            } else {
                new WriteTaskPoints(someData.getPoints(), idTrack).execute();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (countTracks == 1) {
                LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcastSync(new Intent(BROADCAST_SAVE_NEW_TRACKS));
            }
        }
    }


    private static class WriteTaskPoints extends AsyncTask<Void, Void, Void> {
        private List<Point> points = new ArrayList<>();
        private long idTrack;

        private WriteTaskPoints(List<Point> points, long idTrack) {
            this.idTrack = idTrack;
            this.points = points;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int j = 0; j < points.size(); j++) {
                new InquiryBuilder()
                        .table(TABLE_POINT)
                        .set(LAT, points.get(j).getLat())
                        .set(LNG, points.get(j).getLng())
                        .set(TRACK_ID, idTrack)
                        .insert(App.getInstance().getDb());
            }
            return null;
        }
    }
}
