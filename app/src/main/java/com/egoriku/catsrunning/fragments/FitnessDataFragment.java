package com.egoriku.catsrunning.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.FitActivity;
import com.egoriku.catsrunning.activities.TrackOnMapsActivity;
import com.egoriku.catsrunning.adapters.FitnessDataAdapter;
import com.egoriku.catsrunning.adapters.interfaces.IOnItemHandlerListener;
import com.egoriku.catsrunning.loaders.AsyncTracksLoader;
import com.egoriku.catsrunning.models.AllFitnessDataModel;
import com.egoriku.catsrunning.utils.CustomFont;
import com.egoriku.catsrunning.utils.IntentBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.egoriku.catsrunning.helpers.DbActions.updateIsTrackDelete;
import static com.egoriku.catsrunning.helpers.DbActions.updateLikedDigit;
import static com.egoriku.catsrunning.models.Constants.Broadcast.BROADCAST_SAVE_NEW_TRACKS;
import static com.egoriku.catsrunning.models.Constants.ConstantsFirebase.CHILD_TRACKS;
import static com.egoriku.catsrunning.models.Constants.Tags.ARG_SECTION_NUMBER;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_DISTANCE;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_ID;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_LIKED;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_TIME_RUNNING;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_TOKEN;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_TYPE_FIT;

public class FitnessDataFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<AllFitnessDataModel>> {
    private RecyclerView recyclerView;
    private TextView textViewNoTracks;
    private ImageView imageViewNoTracks;
    private ProgressBar progressBar;

    private FitnessDataAdapter adapter;
    private Loader<List<AllFitnessDataModel>> loader;
    private static IFABScroll ifabScroll;


    private BroadcastReceiver broadcastNewTracksSave = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reloadData();
        }
    };


    public FitnessDataFragment() {
    }


    public static FitnessDataFragment newInstance(int sectionNumber, IFABScroll iFabScroll) {
        FitnessDataFragment fragment = new FitnessDataFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        ifabScroll = iFabScroll;
        fragment.setArguments(args);
        return fragment;
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
        progressBar = (ProgressBar) view.findViewById(R.id.fragment_fitness_data_progress_bar);
        recyclerView = (RecyclerView) view.findViewById(R.id.fitness_data_fragment_recycler_view);
        textViewNoTracks = (TextView) view.findViewById(R.id.fragment_fitness_data_text_no_tracks);
        imageViewNoTracks = (ImageView) view.findViewById(R.id.fragment_fitness_data_image_cats_no_track);

        textViewNoTracks.setTypeface(CustomFont.getTypeFace());
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FitnessDataAdapter();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (ifabScroll != null) {
                    ifabScroll.onScrollChange();
                }
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        loader = getLoaderManager().restartLoader(getArguments().getInt(ARG_SECTION_NUMBER), getArguments(), this);

        LocalBroadcastManager.getInstance(App.getInstance())
                .registerReceiver(broadcastNewTracksSave, new IntentFilter(BROADCAST_SAVE_NEW_TRACKS));
    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(App.getInstance()).unregisterReceiver(broadcastNewTracksSave);
    }


    private void setUpAdapter(final List<AllFitnessDataModel> dataModelList) {
        progressBar.setVisibility(View.GONE);
        if (dataModelList.size() == 0) {
            textViewNoTracks.setVisibility(View.VISIBLE);
            imageViewNoTracks.setVisibility(View.VISIBLE);
            setTextNoTracks(textViewNoTracks);
            adapter.clear();
        } else {
            textViewNoTracks.setVisibility(View.GONE);
            imageViewNoTracks.setVisibility(View.GONE);
            adapter.setData(dataModelList);
            recyclerView.setAdapter(adapter);
        }

        adapter.setOnItemListener(new IOnItemHandlerListener() {
            @Override
            public void onClickItem(AllFitnessDataModel item, int position) {
                if (item.getTime() == 0) {
                    startActivity(new IntentBuilder()
                            .context(getActivity())
                            .activity(FitActivity.class)
                            .extra(KEY_TYPE_FIT, item.getTypeFit())
                            .build());
                } else {
                    startActivity(new IntentBuilder()
                            .context(getActivity())
                            .activity(TrackOnMapsActivity.class)
                            .extra(KEY_ID, item.getId())
                            .extra(KEY_DISTANCE, item.getDistance())
                            .extra(KEY_TIME_RUNNING, item.getTime())
                            .extra(KEY_LIKED, item.getLiked())
                            .extra(KEY_TOKEN, item.getTrackToken())
                            .extra(KEY_TYPE_FIT, item.getTypeFit())
                            .build());
                }
            }

            @Override
            public void onLikedClick(AllFitnessDataModel item, int position) {
                int likedState = item.getLiked();

                switch (likedState) {
                    case 0:
                        likedState = 1;
                        updateLikedDigit(likedState, item.getId());
                        dataModelList.get(position).setLiked(likedState);
                        adapter.notifyItemChanged(position);
                        break;

                    case 1:
                        likedState = 0;
                        updateLikedDigit(likedState, item.getId());
                        dataModelList.get(position).setLiked(likedState);
                        adapter.notifyItemChanged(position);
                        break;
                }
            }

            @Override
            public void onLongClick(final AllFitnessDataModel item, final int position) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.fitness_data_fragment_alert_title)
                        .setCancelable(true)
                        .setNegativeButton(R.string.fitness_data_fragment_alert_negative_btn, null)
                        .setPositiveButton(R.string.fitness_data_fragment_alert_positive_btn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateIsTrackDelete(item.getId());

                                if (item.getTime() != 0) {
                                    deleteFromFirebaseDb(item);
                                }

                                dataModelList.remove(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, dataModelList.size());

                                if (dataModelList.size() == 0) {
                                    reloadData();
                                }
                            }
                        })
                        .show();
            }
        });
    }


    private void deleteFromFirebaseDb(AllFitnessDataModel item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (item.getTrackToken() != null && user != null) {
            FirebaseDatabase.getInstance().getReference().child(CHILD_TRACKS).child(user.getUid()).child(item.getTrackToken()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().setValue(null);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void setTextNoTracks(TextView textViewNoTracks) {
        textViewNoTracks.setText(getString(R.string.no_tracks_text));
    }


    @Override
    public Loader<List<AllFitnessDataModel>> onCreateLoader(int id, Bundle args) {
        return new AsyncTracksLoader(getContext(), args);
    }


    @Override
    public void onLoadFinished(Loader<List<AllFitnessDataModel>> loader, List<AllFitnessDataModel> data) {
        setUpAdapter(data);
    }


    @Override
    public void onLoaderReset(Loader loader) {
    }


    private void reloadData() {
        loader.onContentChanged();
    }
}
