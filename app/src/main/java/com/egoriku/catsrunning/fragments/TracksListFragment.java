package com.egoriku.catsrunning.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.MainActivity;
import com.egoriku.catsrunning.activities.ScamperActivity;
import com.egoriku.catsrunning.adapters.TracksListFragmentAdapter;
import com.egoriku.catsrunning.adapters.interfaces.IRecyclerViewListener;
import com.egoriku.catsrunning.models.Firebase.SaveModel;
import com.egoriku.catsrunning.models.MainFragmentTracksModel;
import com.egoriku.catsrunning.models.Point;

import java.util.ArrayList;

public class TracksListFragment extends Fragment {
    public static final String TAG_MAIN_FRAGMENT = "TAG_MAIN_FRAGMENT";
    private static final int UNICODE = 0x1F60E;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private TextView textViewNoTracks;

    private ArrayList<MainFragmentTracksModel> tracksModels = new ArrayList<>();

    private TracksListFragmentAdapter mainFragmentAdapter;
    private FloatingActionButton floatingActionButton;

    private ArrayList<Point> points;
    private SaveModel saveTracksRequestModel;
    private ArrayList<SaveModel> saveModelArrayList;

    private FloatingActionButton fabWalk;
    private FloatingActionButton fabCycling;
    private FloatingActionButton fabRun;

    private Animation fabWalkShow;
    private Animation fabCyclingShow;
    private Animation fabRunShow;

    private Animation fabWalkHide;
    private Animation fabCyclingHide;
    private Animation fabRunHide;

    private boolean fabStatus;

    public TracksListFragment() {
    }

    public static TracksListFragment newInstance() {
        return new TracksListFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_main_activity, TAG_MAIN_FRAGMENT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "+");

        if (App.getInstance().getState() == null) {
            App.getInstance().createState();
        }

       /*if (App.getInstance().getState().isFirstStart()) {
            saveTracksOnServer();*/
        //  saveTracksOnServer();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.main_fragment_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        textViewNoTracks = (TextView) view.findViewById(R.id.list_fragment_no_more_tracks);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button);
        fabWalk = (FloatingActionButton) view.findViewById(R.id.fab_walk);
        fabCycling = (FloatingActionButton) view.findViewById(R.id.fab_cycling);
        fabRun = (FloatingActionButton) view.findViewById(R.id.fab_run);

        recyclerView.setLayoutManager(new LinearLayoutManager(App.getInstance()));

        fabRunHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_run_hide);
        fabRunShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_run_show);
        fabWalkShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_walk_show);
        fabWalkHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_walk_hide);
        fabCyclingShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_cycling_show);
        fabCyclingHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_cycling_hide);
        fabStatus = false;

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fabStatus){
                    showFabs(fabStatus);
                    fabStatus=false;
                }else {
                    showFabs(fabStatus);
                    fabStatus=true;
                }
            }
        });

        fabWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ScamperActivity.class));
            }
        });

        fabCycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ScamperActivity.class));
            }
        });

        fabRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ScamperActivity.class));
            }
        });


        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               /* if (App.getInstance().getState().isStartSyncTracks() || !App.getInstance().getState().isStartSync()) {
                    saveTracksOnServer();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(App.getInstance(), getString(R.string.now_is_sync), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }*/
            }
        });

        getTracksFromDb();
        textViewNoTracks.setText(null);
        mainFragmentAdapter = new TracksListFragmentAdapter(tracksModels);

        if (tracksModels.size() == 0) {
            textViewNoTracks.setText(String.format("%s%s", getString(R.string.tracks_list_no_more_tracks), getEmojiByUnicode(UNICODE)));
        } else {
            recyclerView.setAdapter(mainFragmentAdapter);
            recyclerView.hasFixedSize();
        }

        mainFragmentAdapter.setOnItemClickListener(new IRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                changeFragment(tracksModels.get(position).getId(), tracksModels.get(position).getDistance(), tracksModels.get(position).getTimeRunning());
            }

            @Override
            public void onLikedClick(int position) {
                int likedDigit = 0;
                Cursor cursor = App.getInstance().getDb().rawQuery("SELECT Tracks.liked AS liked FROM Tracks WHERE Tracks._id = ?",
                        new String[]{String.valueOf(tracksModels.get(position).getId())});

                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        likedDigit = cursor.getInt(cursor.getColumnIndexOrThrow("liked"));
                    }
                    cursor.close();
                }

                switch (likedDigit) {
                    case 0:
                        likedDigit = 1;
                        updateLikedDigit(likedDigit, tracksModels.get(position).getId());
                        mainFragmentAdapter.notifyDataSetChanged();
                        break;

                    case 1:
                        likedDigit = 0;
                        updateLikedDigit(likedDigit, tracksModels.get(position).getId());
                        mainFragmentAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
        return view;
    }

    private void showFabs(boolean status) {
        FrameLayout.LayoutParams layoutParamsFabRun = (FrameLayout.LayoutParams) fabRun.getLayoutParams();
        FrameLayout.LayoutParams layoutParamsFabWalk = (FrameLayout.LayoutParams) fabWalk.getLayoutParams();
        FrameLayout.LayoutParams layoutParamsFabCycling = (FrameLayout.LayoutParams) fabCycling.getLayoutParams();

        if(status) {
            layoutParamsFabRun.rightMargin -= (int) (fabRun.getWidth() * 1.7);
            layoutParamsFabRun.bottomMargin -= (int) (fabRun.getHeight() * 0.25);
            fabRun.setLayoutParams(layoutParamsFabRun);

            layoutParamsFabWalk.rightMargin -= (int) (fabWalk.getWidth() * 1.5);
            layoutParamsFabWalk.bottomMargin -= (int) (fabWalk.getHeight() * 1.5);
            fabWalk.setLayoutParams(layoutParamsFabWalk);

            layoutParamsFabCycling.rightMargin -= (int) (fabCycling.getWidth() * 0.25);
            layoutParamsFabCycling.bottomMargin -= (int) (fabCycling.getHeight() * 1.7);
            fabCycling.setLayoutParams(layoutParamsFabCycling);

            fabRun.setClickable(false);
            fabWalk.setClickable(false);
            fabCycling.setClickable(false);

            fabRun.startAnimation(fabRunHide);
            fabWalk.startAnimation(fabWalkHide);
            fabCycling.startAnimation(fabCyclingHide);
        }else {
            layoutParamsFabRun.rightMargin += (int) (fabRun.getWidth() * 1.7);
            layoutParamsFabRun.bottomMargin += (int) (fabRun.getHeight() * 0.25);
            fabRun.setLayoutParams(layoutParamsFabRun);

            layoutParamsFabWalk.rightMargin += (int) (fabWalk.getWidth() * 1.5);
            layoutParamsFabWalk.bottomMargin += (int) (fabWalk.getHeight() * 1.5);
            fabWalk.setLayoutParams(layoutParamsFabWalk);

            layoutParamsFabCycling.rightMargin += (int) (fabCycling.getWidth() * 0.25);
            layoutParamsFabCycling.bottomMargin += (int) (fabCycling.getHeight() * 1.7);
            fabCycling.setLayoutParams(layoutParamsFabCycling);

            fabRun.setClickable(true);
            fabWalk.setClickable(true);
            fabCycling.setClickable(true);

            fabRun.startAnimation(fabRunShow);
            fabWalk.startAnimation(fabWalkShow);
            fabCycling.startAnimation(fabCyclingShow);
        }
    }


    private void getTracksFromDb() {
        Log.e("getTracksFromDb", "+");
        tracksModels.clear();
        Cursor cursor = App.getInstance().getDb().rawQuery("SELECT Tracks._id AS id, Tracks.beginsAt AS date, Tracks.time AS timeRunning, Tracks.distance AS distance, Tracks.liked as liked FROM Tracks ORDER BY date DESC", null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {
                    MainFragmentTracksModel mainModel = new MainFragmentTracksModel();
                    mainModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    mainModel.setDate(cursor.getInt(cursor.getColumnIndexOrThrow("date")));
                    mainModel.setTimeRunning(cursor.getInt(cursor.getColumnIndexOrThrow("timeRunning")));
                    mainModel.setDistance(cursor.getInt(cursor.getColumnIndexOrThrow("distance")));
                    mainModel.setLiked(cursor.getInt(cursor.getColumnIndexOrThrow("liked")));

                    tracksModels.add(mainModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "+");

        getTracksFromDb();
        mainFragmentAdapter.setAdapterData(tracksModels);

       /* if (App.getInstance().getState().isFirstStart()) {
            Log.e("isFS", "+");
            showProgressDialog();
        }

        if (App.getInstance().getState().isFirstStart() && (App.getInstance().getState().isStartSync() || App.getInstance().getState().isStartSyncTracks())) {
            Log.e("isFS + isSTask", "+");
            showProgressDialog();
        }*/

        /*LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(broadcastSaveError, new IntentFilter(SaveProvider.BROADCAST_SAVE_ERROR));
        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(broadcastSaveSuccess, new IntentFilter(SaveProvider.BROADCAST_SAVE_FINISH));
        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(broadcastTracksSuccess, new IntentFilter(TracksProvider.BROADCAST_TRACKS_SUCCESS));
        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(broadcastTracksError, new IntentFilter(TracksProvider.BROADCAST_TRACKS_ERROR));
        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(broadcastPointsSuccess, new IntentFilter(PointsProvider.BROADCAST_POINTS_SUCCESS));
        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(broadcastPointsFinish, new IntentFilter(PointsProvider.BROADCAST_POINTS_FINISH));
        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(broadcastPointsError, new IntentFilter(PointsProvider.BROADCAST_POINTS_ERROR));
        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver(broadcastNoNewTracks, new IntentFilter(TracksProvider.BROADCAST_NO_NEW_TRACKS));*/
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "+");
        dismissProgressDialog();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

    /*    LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastSaveError);
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastSaveSuccess);
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastTracksSuccess);
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastTracksError);
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastPointsSuccess);
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastPointsFinish);
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastPointsError);
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastNoNewTracks);*/
    }


  /*  private BroadcastReceiver broadcastSaveError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("broadcastSaveError", "+");
            dismissProgressDialog();
            swipeRefreshLayout.setRefreshing(false);
            App.getInstance().getState().setFirstStartFalse();

            if (App.getInstance().getState().getSaveAfterRequestModel() == null) {
                Toast.makeText(App.getInstance(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else {
                switch (App.getInstance().getState().getSaveAfterRequestModel().getCode()) {
                    case "INVALID_TOKEN": //неверный авторизационный токен
                        Toast.makeText(App.getInstance(), getString(R.string.error_token), Toast.LENGTH_SHORT).show();
                        break;

                    case "INVALID_FIELDS": //поля beginsAt, time или distance пустые, либо в них неверные значения (например, не числовые)
                        Toast.makeText(App.getInstance(), getString(R.string.fields_error), Toast.LENGTH_SHORT).show();
                        break;

                    case "NO_POINTS": //нет поля points, либо в нём не массив, либо массив пуст;
                        Toast.makeText(App.getInstance(), getString(R.string.no_points), Toast.LENGTH_SHORT).show();
                        break;


                    case "INVALID_POINTS": //какая-то одна (из всех) точек неправильная - либо нет полей lng или lat, либо они не числовые;
                        Toast.makeText(App.getInstance(), getString(R.string.error_points), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };


    private BroadcastReceiver broadcastSaveSuccess = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("broadcastSaveSuccess", "+");
            saveIdFromServerToDb();
            TracksProvider.tracksRequest(App.getInstance().getState().getToken());
        }
    };


    private BroadcastReceiver broadcastTracksSuccess = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("broadcastTracksSuccess", "+");
            Toast.makeText(App.getInstance(), getString(R.string.no_new_tracks), Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
            swipeRefreshLayout.setRefreshing(false);
            App.getInstance().getState().setStartSync(false);
            App.getInstance().getState().setStartSyncTracks(false);
            App.getInstance().getState().setFirstStartFalse();
        }
    };


    private BroadcastReceiver broadcastTracksError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("broadcastTracksError", "+");
            dismissProgressDialog();
            swipeRefreshLayout.setRefreshing(false);
            App.getInstance().getState().setFirstStartFalse();

            if (App.getInstance().getState().getTracksAfterRequestModel() == null) {
                Toast.makeText(App.getInstance(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else {
                switch (App.getInstance().getState().getTracksAfterRequestModel().getCode()) {
                    case "INVALID_TOKEN":
                        Toast.makeText(App.getInstance(), getString(R.string.error_token), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };


    private BroadcastReceiver broadcastNoNewTracks = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("broadcastNoNewTracks", "+");
            if (!App.getInstance().getState().isNoNewTracks()) {
                dismissProgressDialog();

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                App.getInstance().getState().setFirstStartFalse();
                App.getInstance().getState().setStartSyncTracks(false);
                App.getInstance().getState().setStartSync(false);
            }
        }
    };


    private BroadcastReceiver broadcastPointsSuccess = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("broadcastPointsSuccess", "+");
            App.getInstance().getState().setFirstStartFalse();
        }
    };


    private BroadcastReceiver broadcastPointsFinish = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("broadcastPointsFinish", "+");
            dismissProgressDialog();
            swipeRefreshLayout.setRefreshing(false);
            getTracksFromDb();
            mainFragmentAdapter.setAdapterData(tracksModels);

            textViewNoTracks.setText(null);
            if (tracksModels.size() == 0) {
                textViewNoTracks.setText(String.format("%s%s", getString(R.string.no_tracks_list), getEmojiByUnicode(UNICODE)));
            } else {
                recyclerView.setAdapter(mainFragmentAdapter);
                recyclerView.hasFixedSize();
            }
        }
    };


    private BroadcastReceiver broadcastPointsError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (App.getInstance().getState().getPointsAfterRequestModel() == null) {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else {
                switch (App.getInstance().getState().getPointsAfterRequestModel().getCode()) {
                    case "INVALID_TOKEN": //неверный авторизационный токен;
                        Toast.makeText(App.getInstance(), getString(R.string.error_token), Toast.LENGTH_SHORT).show();
                        break;

                    case "INVALID_ID": //неправильный ID трека
                        Toast.makeText(App.getInstance(), getString(R.string.error_id_track), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };*/


    private void saveIdFromServerToDb() {
       /* for (int i = 0; i < App.getInstance().getState().getSaveModelArrayList().size(); i++) {
            SQLiteStatement statement = App.getInstance().getDb().compileStatement("UPDATE Tracks SET idTrackOnServer = ? WHERE Tracks._id = ?");

            statement.bindLong(1, App.getInstance().getState().getSaveModelArrayList().get(i).getId());
            statement.bindLong(2, App.getInstance().getState().getSaveModelArrayList().get(i).getIdApp());

            try {
                statement.execute();
            } finally {
                statement.close();
            }
        }*/
    }


    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }


    private void updateLikedDigit(int likedDigit, int position) {
        SQLiteStatement statement = App.getInstance().getDb().compileStatement("UPDATE Tracks SET liked=? WHERE _id = ?");

        statement.bindLong(1, likedDigit);
        statement.bindLong(2, position);

        try {
            statement.execute();
        } finally {
            statement.close();
        }
    }


    private void showProgressDialog() {
        if (progressDialog != null) {
            return;
        }

        progressDialog = ProgressDialog.show(
                getActivity(),
                getString(R.string.tracks_list_fragment_progress_dialog_title),
                getString(R.string.tracks_list_fragment_progress_dialog_message),
                true,
                false
        );
    }


    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    private void changeFragment(int position, int distance, int timeRunning) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(
                R.id.fragment_container,
                TrackFragment.newInstance(position, distance, timeRunning),
                TrackFragment.TAG_TRACK_FRAGMENT);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(TrackFragment.TAG_TRACK_FRAGMENT);
        transaction.commit();
        mainFragmentAdapter.clear();
    }
}
