package com.egoriku.catsrunning.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
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
import com.egoriku.catsrunning.activities.TrackOnMapsActivity;
import com.egoriku.catsrunning.adapters.AllFitnessDataAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;

import java.util.ArrayList;

public class FitnessDataFragment extends Fragment {
    public static final String TAG_MAIN_FRAGMENT = "TAG_MAIN_FRAGMENT";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int UNICODE = 0x1F63A;

    private RecyclerView recyclerView;
    private TextView textViewNoTracks;
    private AppBarLayout appBarLayout;

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
    private FastItemAdapter<AllFitnessDataAdapter> fastAdapter;
    private ArrayList<AllFitnessDataAdapter> tracksModels;


    public FitnessDataFragment() {
    }

    public static FitnessDataFragment newInstance(int sectionNumber) {
        FitnessDataFragment fragment = new FitnessDataFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness_data, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.main_fragment_recycler_view);
        textViewNoTracks = (TextView) view.findViewById(R.id.list_fragment_no_more_tracks);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.fragment_fitness_data_appbar);
        fabMain = (FloatingActionButton) view.findViewById(R.id.floating_button);
        fabWalk = (FloatingActionButton) view.findViewById(R.id.fab_walk);
        fabCycling = (FloatingActionButton) view.findViewById(R.id.fab_cycling);
        fabRun = (FloatingActionButton) view.findViewById(R.id.fab_run);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fabWalkShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_walk_show);
        fabWalkHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_walk_hide);
        fabRunShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_run_show);
        fabRunHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_run_hide);
        fabCyclingShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_cycling_show);
        fabCyclingHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_cycling_hide);
        fabStatus = false;

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabStatus) {
                    for (int i = 0; i < 20; i++) {
                        SQLiteStatement statement = App.getInstance().getDb().compileStatement(
                                "INSERT INTO Tracks (beginsAt, time, distance) VALUES (?, ?, ?)"
                        );

                        statement.bindDouble(1, 1474814677);
                        statement.bindDouble(2, 68506);
                        statement.bindLong(3, 6969);

                        try {
                            statement.execute();
                        } finally {
                            statement.close();
                        }
                    }
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

        fastAdapter = new FastItemAdapter<>();
        fastAdapter.withSelectable(true);
        clickListenerHelper = new ClickListenerHelper<>(fastAdapter);
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
        if (fabStatus) {
            changeFabState(true);
        }
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
            appBarLayout.setExpanded(false);
            textViewNoTracks.setText(String.format(getString(R.string.tracks_list_no_more_tracks), getEmojiByUnicode(UNICODE)));
        } else {
            appBarLayout.setExpanded(true);
            fastAdapter.withSelectable(true);
            fastAdapter.withSelectOnLongClick(true);
            fastAdapter.set(tracksModels);
            recyclerView.setAdapter(fastAdapter);
        }

        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<AllFitnessDataAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<AllFitnessDataAdapter> adapter, AllFitnessDataAdapter item, int position) {
                Intent intentTrackOnMaps = new Intent(getActivity(), TrackOnMapsActivity.class);
                intentTrackOnMaps.putExtra(TrackOnMapsActivity.KEY_ID, item.getId());
                intentTrackOnMaps.putExtra(TrackOnMapsActivity.KEY_DISTANCE, item.getDistance());
                intentTrackOnMaps.putExtra(TrackOnMapsActivity.KEY_TIME_RUNNING, item.getTime());
                startActivity(intentTrackOnMaps);
                return false;
            }
        });

        fastAdapter.withOnCreateViewHolderListener(new FastAdapter.OnCreateViewHolderListener() {
            @Override
            public RecyclerView.ViewHolder onPreCreateViewHolder(ViewGroup parent, int viewType) {
                return fastAdapter.getTypeInstance(viewType).getViewHolder(parent);
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
                                    fastAdapter.notifyAdapterDataSetChanged();
                                    break;

                                case 1:
                                    likedState = 0;
                                    updateLikedDigit(likedState, item.getId());
                                    fastAdapter.notifyAdapterDataSetChanged();
                                    break;
                            }
                        }
                    });
                }
                return viewHolder;
            }
        });

        fastAdapter.withOnPreLongClickListener(new FastAdapter.OnLongClickListener<AllFitnessDataAdapter>() {
            @Override
            public boolean onLongClick(View v, IAdapter<AllFitnessDataAdapter> adapter, final AllFitnessDataAdapter item, int position) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Удалить этот трек ?")
                        .setCancelable(true)
                        .setNegativeButton("Отмена", null)
                        .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                App.getInstance().getTracksReference().equalTo(item.getTrackToken()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.e("delete", "+");
                                        dataSnapshot.getRef().setValue(null);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                                    }
                                });
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    private void changeFabState(boolean status) {
        FrameLayout.LayoutParams layoutParamsFabRun = (FrameLayout.LayoutParams) fabRun.getLayoutParams();
        FrameLayout.LayoutParams layoutParamsFabWalk = (FrameLayout.LayoutParams) fabWalk.getLayoutParams();
        FrameLayout.LayoutParams layoutParamsFabCycling = (FrameLayout.LayoutParams) fabCycling.getLayoutParams();

        if (status) {
            layoutParamsFabWalk.rightMargin -= (int) (fabWalk.getWidth() * 1.4);
            layoutParamsFabWalk.bottomMargin -= (int) (fabWalk.getHeight() * 0.0);
            fabWalk.setLayoutParams(layoutParamsFabWalk);

            layoutParamsFabRun.rightMargin -= (int) (fabRun.getWidth() * 1.1);
            layoutParamsFabRun.bottomMargin -= (int) (fabRun.getHeight() * 1.1);
            fabRun.setLayoutParams(layoutParamsFabRun);

            layoutParamsFabCycling.rightMargin -= (int) (fabCycling.getWidth() * 0.0);
            layoutParamsFabCycling.bottomMargin -= (int) (fabCycling.getHeight() * 1.4);
            fabCycling.setLayoutParams(layoutParamsFabCycling);

            fabRun.setClickable(false);
            fabWalk.setClickable(false);
            fabCycling.setClickable(false);

            fabRun.startAnimation(fabRunHide);
            fabWalk.startAnimation(fabWalkHide);
            fabCycling.startAnimation(fabCyclingHide);
        } else {
            layoutParamsFabWalk.rightMargin += (int) (fabWalk.getWidth() * 1.4);
            layoutParamsFabWalk.bottomMargin += (int) (fabWalk.getHeight() * 0.0);
            fabWalk.setLayoutParams(layoutParamsFabWalk);

            layoutParamsFabRun.rightMargin += (int) (fabRun.getWidth() * 1.1);
            layoutParamsFabRun.bottomMargin += (int) (fabRun.getHeight() * 1.1);
            fabRun.setLayoutParams(layoutParamsFabRun);

            layoutParamsFabCycling.rightMargin += (int) (fabCycling.getWidth() * 0.0);
            layoutParamsFabCycling.bottomMargin += (int) (fabCycling.getHeight() * 1.4);
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
        tracksModels = new ArrayList<>();
        Cursor cursor = App.getInstance().getDb().rawQuery("SELECT Tracks._id AS id, Tracks.beginsAt AS date, Tracks.time AS timeRunning, Tracks.distance AS distance, Tracks.liked as liked, Tracks.trackToken AS trackToken FROM Tracks ORDER BY date DESC", null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {
                    AllFitnessDataAdapter listAdapter = new AllFitnessDataAdapter();
                    listAdapter.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    listAdapter.setBeginsAt(cursor.getInt(cursor.getColumnIndexOrThrow("date")));
                    listAdapter.setTime(cursor.getInt(cursor.getColumnIndexOrThrow("timeRunning")));
                    listAdapter.setDistance(cursor.getInt(cursor.getColumnIndexOrThrow("distance")));
                    listAdapter.setLiked(cursor.getInt(cursor.getColumnIndexOrThrow("liked")));
                    listAdapter.setTrackToken(cursor.getString(cursor.getColumnIndexOrThrow("trackToken")));
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


   /* class ActionBarCallBack implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.cab, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Checkbox Selected");
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            undoHelper.remove(recyclerView, "Item removed", "Undo", Snackbar.LENGTH_LONG, fastAdapter.getSelections());
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mode.finish();
        }
    }*/
}
