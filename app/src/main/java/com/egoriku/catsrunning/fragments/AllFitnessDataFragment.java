package com.egoriku.catsrunning.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
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
import com.egoriku.catsrunning.adapters.AllFitnessDataAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;

import java.util.ArrayList;

public class AllFitnessDataFragment extends Fragment {
    public static final String TAG_MAIN_FRAGMENT = "TAG_MAIN_FRAGMENT";
    private static final int UNICODE = 0x1F63A;

    private RecyclerView recyclerView;
    private TextView textViewNoTracks;

    private FloatingActionButton fabMain;
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
    private ClickListenerHelper<AllFitnessDataAdapter> clickListenerHelper;
    private FastItemAdapter<AllFitnessDataAdapter> fastItemAdapter;

    private ArrayList<AllFitnessDataAdapter> tracksModels;


    public AllFitnessDataFragment() {
    }


    public static AllFitnessDataFragment newInstance() {
        return new AllFitnessDataFragment();
    }


    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_main_activity, TAG_MAIN_FRAGMENT);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.getInstance().getState() == null) {
            App.getInstance().createState();
        }
        tracksModels = new ArrayList<>();
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.main_fragment_recycler_view);
        textViewNoTracks = (TextView) view.findViewById(R.id.list_fragment_no_more_tracks);
        fabMain = (FloatingActionButton) view.findViewById(R.id.floating_button);
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

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabStatus) {
                    changeFabState(fabStatus);
                    fabStatus = false;
                } else {
                    changeFabState(fabStatus);
                    fabStatus = true;
                }
            }
        });

        fabWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFabState(true);
                startActivity(new Intent(getActivity(), ScamperActivity.class));
            }
        });

        fabCycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFabState(true);
                startActivity(new Intent(getActivity(), ScamperActivity.class));
            }
        });

        fabRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFabState(true);
                startActivity(new Intent(getActivity(), ScamperActivity.class));
            }
        });

        fastItemAdapter = new FastItemAdapter<>();
        fastItemAdapter.withSelectable(true);
        clickListenerHelper = new ClickListenerHelper<>(fastItemAdapter);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        fabStatus = false;
        LocalBroadcastManager.getInstance(App.getInstance()).
                registerReceiver(broadcastNewTracksSave, new IntentFilter(MainActivity.BROADCAST_SAVE_NEW_TRACKS));

        setUpAdapter();
    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastNewTracksSave);
    }


    private BroadcastReceiver broadcastNewTracksSave = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setUpAdapter();
        }
    };


    private void setUpAdapter() {
        getTracksFromDb();
        textViewNoTracks.setText(null);

        if (tracksModels.size() == 0) {
            textViewNoTracks.setText(String.format(getString(R.string.tracks_list_no_more_tracks), getEmojiByUnicode(UNICODE)));
        } else {
            fastItemAdapter.set(tracksModels);
            recyclerView.setAdapter(fastItemAdapter);
        }

        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<AllFitnessDataAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<AllFitnessDataAdapter> adapter, AllFitnessDataAdapter item, int position) {
                changeFragment(item.getId(), item.getDistance(), item.getTime());
                return false;
            }
        });

        fastItemAdapter.withOnCreateViewHolderListener(new FastAdapter.OnCreateViewHolderListener() {
            @Override
            public RecyclerView.ViewHolder onPreCreateViewHolder(ViewGroup parent, int viewType) {
                return fastItemAdapter.getTypeInstance(viewType).getViewHolder(parent);
            }

            @Override
            public RecyclerView.ViewHolder onPostCreateViewHolder(final RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof AllFitnessDataAdapter.ViewHolder) {
                    clickListenerHelper.listen(viewHolder, ((AllFitnessDataAdapter.ViewHolder) viewHolder).relativeLayout, new ClickListenerHelper.OnClickListener<AllFitnessDataAdapter>() {
                        @Override
                        public void onClick(View v, int position, AllFitnessDataAdapter item) {
                            int likedState = item.getLiked();

                            switch (likedState) {
                                case 0:
                                    likedState = 1;
                                    updateLikedDigit(likedState, item.getId());
                                    fastItemAdapter.notifyAdapterDataSetChanged();
                                    break;

                                case 1:
                                    likedState = 0;
                                    updateLikedDigit(likedState, item.getId());
                                    fastItemAdapter.notifyAdapterDataSetChanged();
                                    break;
                            }
                        }
                    });
                }
                return viewHolder;
            }
        });
    }


    private void changeFabState(boolean status) {
        FrameLayout.LayoutParams layoutParamsFabRun = (FrameLayout.LayoutParams) fabRun.getLayoutParams();
        FrameLayout.LayoutParams layoutParamsFabWalk = (FrameLayout.LayoutParams) fabWalk.getLayoutParams();
        FrameLayout.LayoutParams layoutParamsFabCycling = (FrameLayout.LayoutParams) fabCycling.getLayoutParams();

        if (status) {
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
        } else {
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
                    AllFitnessDataAdapter listAdapter = new AllFitnessDataAdapter();
                    listAdapter.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    listAdapter.setBeginsAt(cursor.getInt(cursor.getColumnIndexOrThrow("date")));
                    listAdapter.setTime(cursor.getInt(cursor.getColumnIndexOrThrow("timeRunning")));
                    listAdapter.setDistance(cursor.getInt(cursor.getColumnIndexOrThrow("distance")));
                    listAdapter.setLiked(cursor.getInt(cursor.getColumnIndexOrThrow("liked")));

                    tracksModels.add(listAdapter);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
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


    private void changeFragment(int position, long distance, long timeRunning) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(
                R.id.fragment_container,
                TrackFragment.newInstance(position, distance, timeRunning),
                TrackFragment.TAG_TRACK_FRAGMENT);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(TrackFragment.TAG_TRACK_FRAGMENT);
        transaction.commit();
    }
}
