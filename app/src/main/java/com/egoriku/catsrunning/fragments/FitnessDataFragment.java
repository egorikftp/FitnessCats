package com.egoriku.catsrunning.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.FitActivity;
import com.egoriku.catsrunning.activities.TrackOnMapsActivity;
import com.egoriku.catsrunning.adapters.FitnessDataHolder;
import com.egoriku.catsrunning.models.Firebase.SaveModel;
import com.egoriku.catsrunning.utils.CustomFont;
import com.egoriku.catsrunning.utils.FirebaseUtils;
import com.egoriku.catsrunning.utils.IntentBuilder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

import static com.egoriku.catsrunning.models.Constants.Extras.KEY_TYPE_FIT;
import static com.egoriku.catsrunning.models.Constants.FirebaseFields.CHILD_TRACKS;
import static com.egoriku.catsrunning.models.Constants.FirebaseFields.TYPE_FIT;
import static com.egoriku.catsrunning.models.Constants.Tags.ARG_SECTION_NUMBER;

public class FitnessDataFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textViewNoTracks;
    private ImageView imageViewNoTracks;
    private ProgressBar progressBar;

    private FirebaseRecyclerAdapter adapter;
    private static IFABScroll ifabScroll;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness_data, container, false);

        int typeFit = getArguments().getInt(ARG_SECTION_NUMBER);

        progressBar = (ProgressBar) view.findViewById(R.id.fragment_fitness_data_progress_bar);
        recyclerView = (RecyclerView) view.findViewById(R.id.fitness_data_fragment_recycler_view);
        textViewNoTracks = (TextView) view.findViewById(R.id.fragment_fitness_data_text_no_tracks);
        imageViewNoTracks = (ImageView) view.findViewById(R.id.fragment_fitness_data_image_cats_no_track);

        textViewNoTracks.setTypeface(CustomFont.getTypeFace());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        hideNoTracks();
        showLoading(true);

        Query query = databaseReference
                .child(CHILD_TRACKS)
                .child(user.getUid())
                .orderByChild(TYPE_FIT)
                .equalTo(typeFit);

        adapter = new FirebaseRecyclerAdapter<SaveModel, FitnessDataHolder>(SaveModel.class, R.layout.adapter_fitness_data_fragment, FitnessDataHolder.class, query) {
            @Override
            protected void populateViewHolder(final FitnessDataHolder viewHolder, SaveModel model, int position) {
                showLoading(false);
                viewHolder.setData(model, getContext());
            }

            @Override
            public FitnessDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                FitnessDataHolder holder = super.onCreateViewHolder(parent, viewType);

                holder.setOnClickListener(new FitnessDataHolder.ClickListener() {

                    @Override
                    public void onClickItem(int position) {
                        SaveModel saveModel = (SaveModel) adapter.getItem(position);

                        if (saveModel.getTime() == 0) {
                            startActivity(new IntentBuilder()
                                    .context(getActivity())
                                    .activity(FitActivity.class)
                                    .extra(KEY_TYPE_FIT, saveModel.getTypeFit())
                                    .build());
                        } else {
                            TrackOnMapsActivity.start(getActivity(), saveModel);
                        }
                    }

                    @Override
                    public void onFavoriteClick(int position) {
                        SaveModel adapterItem = (SaveModel) adapter.getItem(position);
                        adapterItem.setFavorite(!adapterItem.isFavorite());
                        FirebaseUtils.updateTrackFavorire(adapterItem, getContext());
                    }

                    @Override
                    public void onLongClick(final int position) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.fitness_data_fragment_alert_title)
                                .setCancelable(true)
                                .setNegativeButton(R.string.fitness_data_fragment_alert_negative_btn, null)
                                .setPositiveButton(R.string.fitness_data_fragment_alert_positive_btn, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseUtils.removeTrack((SaveModel) adapter.getItem(position), getContext());

                                    }
                                })
                                .show();
                    }
                });
                return holder;
            }
        };

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (adapter.getItemCount() == 0) {
                    showNoTracks();
                    showLoading(false);
                } else {
                    hideNoTracks();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.d(databaseError.getMessage());
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (adapter.getItemCount() == 0) {
                    showNoTracks();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (ifabScroll != null) {
                    ifabScroll.onScrollChange();
                }
            }
        });

        recyclerView.setAdapter(adapter);
        return view;
    }

    private void showLoading(boolean isShowLoading) {
        progressBar.setVisibility(isShowLoading ? View.VISIBLE : View.GONE);
    }

    private void showNoTracks() {
        textViewNoTracks.setVisibility(View.VISIBLE);
        imageViewNoTracks.setVisibility(View.VISIBLE);
        textViewNoTracks.setText(R.string.no_tracks_text);
    }

    private void hideNoTracks() {
        textViewNoTracks.setVisibility(View.GONE);
        imageViewNoTracks.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
}
