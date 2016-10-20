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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.ScamperActivity;
import com.egoriku.catsrunning.activities.TrackOnMapsActivity;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.egoriku.catsrunning.adapters.AllFitnessDataAdapter;
import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;

import java.util.ArrayList;

import static com.egoriku.catsrunning.models.State.AND;
import static com.egoriku.catsrunning.models.State.BEGINS_AT;
import static com.egoriku.catsrunning.models.State.DISTANCE;
import static com.egoriku.catsrunning.models.State.IS_TRACK_DELETE_EQ;
import static com.egoriku.catsrunning.models.State.IS_TRACK_DELETE_FALSE;
import static com.egoriku.catsrunning.models.State.IS_TRACK_DELETE_TRUE;
import static com.egoriku.catsrunning.models.State.LIKED;
import static com.egoriku.catsrunning.models.State.TABLE_TRACKS;
import static com.egoriku.catsrunning.models.State.TIME;
import static com.egoriku.catsrunning.models.State.TRACK_TOKEN;
import static com.egoriku.catsrunning.models.State.TYPE_FIT;
import static com.egoriku.catsrunning.models.State._ID;

public class FitnessDataFragment extends Fragment {
    public static final String TAG_MAIN_FRAGMENT = "TAG_MAIN_FRAGMENT";
    private static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";

    private RecyclerView recyclerView;
    private TextView textViewNoTracks;
    private ImageView imageViewNoTracks;
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
        ((TracksActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_main_activity, TAG_MAIN_FRAGMENT);
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
        textViewNoTracks = (TextView) view.findViewById(R.id.fragment_fitness_data_text_no_tracks);
        imageViewNoTracks = (ImageView) view.findViewById(R.id.fragment_fitness_data_image);
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
                registerReceiver(broadcastNewTracksSave, new IntentFilter(TracksActivity.BROADCAST_SAVE_NEW_TRACKS));

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
        imageViewNoTracks.setVisibility(View.GONE);
        textViewNoTracks.setVisibility(View.GONE);

        if (tracksModels.size() == 0) {
            appBarLayout.setExpanded(false);
            imageViewNoTracks.setVisibility(View.VISIBLE);
            textViewNoTracks.setVisibility(View.VISIBLE);
        } else {
            appBarLayout.setExpanded(true);
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
                    clickListenerHelper.listen(viewHolder, ((AllFitnessDataAdapter.ViewHolder) viewHolder).imageViewLiked, new ClickListenerHelper.OnClickListener<AllFitnessDataAdapter>() {
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
            public boolean onLongClick(View v, IAdapter<AllFitnessDataAdapter> adapter, final AllFitnessDataAdapter item, final int position) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.fitness_data_fragment_alert_title)
                        .setCancelable(true)
                        .setNegativeButton(R.string.fitness_data_fragment_alert_negative_btn, null)
                        .setPositiveButton(R.string.fitness_data_fragment_alert_positive_btn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new InquiryBuilder()
                                        .set(IS_TRACK_DELETE_EQ, IS_TRACK_DELETE_TRUE)
                                        .updateWhere(TABLE_TRACKS + _ID, String.valueOf(item.getId()));
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                App.getInstance().getTracksReference().child(user.getUid()).child(item.getTrackToken()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        dataSnapshot.getRef().setValue(null);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                tracksModels.remove(position);
                                fastAdapter.notifyItemRemoved(position);
                                fastAdapter.notifyItemRangeChanged(position, tracksModels.size());

                                if (tracksModels.size() == 0) {
                                    fastAdapter.clear();
                                }
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
        tracksModels = new ArrayList<>();

        Cursor cursorTracks = new InquiryBuilder()
                .get(_ID, BEGINS_AT, TIME, DISTANCE, LIKED, TRACK_TOKEN, TYPE_FIT)
                .from(TABLE_TRACKS)
                .where(true, IS_TRACK_DELETE_EQ + " " + IS_TRACK_DELETE_FALSE + " " + AND + " " + TYPE_FIT + "=" + String.valueOf(getArguments().getInt(ARG_SECTION_NUMBER)))
                .orderBy(BEGINS_AT)
                .desc()
                .select();

        DbCursor cursor = new DbCursor(cursorTracks);
        if (cursor.isValid()) {
            do {
                AllFitnessDataAdapter listAdapter = new AllFitnessDataAdapter();
                listAdapter.setId(cursor.getInt(_ID));
                listAdapter.setBeginsAt(cursor.getInt(BEGINS_AT));
                listAdapter.setTime(cursor.getInt(TIME));
                listAdapter.setDistance(cursor.getInt(DISTANCE));
                listAdapter.setLiked(cursor.getInt(LIKED));
                listAdapter.setTrackToken(cursor.getString(TRACK_TOKEN));
                listAdapter.setTypeFit(cursor.getInt(TYPE_FIT));
                tracksModels.add(listAdapter);
            } while (cursorTracks.moveToNext());
        }
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
}
