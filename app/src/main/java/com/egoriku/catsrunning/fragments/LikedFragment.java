package com.egoriku.catsrunning.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.FitActivity;
import com.egoriku.catsrunning.ui.activity.TrackMapActivity;
import com.egoriku.catsrunning.adapters.FitnessDataHolder;
import com.egoriku.catsrunning.data.commons.TracksModel;
import com.egoriku.catsrunning.models.Constants;
import com.egoriku.catsrunning.ui.activity.TracksActivity;
import com.egoriku.catsrunning.utils.FirebaseUtils;
import com.egoriku.catsrunning.utils.IntentBuilder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

import static com.egoriku.catsrunning.models.Constants.FirebaseFields.IS_FAVORIRE;
import static com.egoriku.catsrunning.models.Constants.FirebaseFields.TRACKS;

public class LikedFragment extends Fragment {

    private static final int UNICODE_SAD_CAT = 0x1F640;
    private static final int DURATION = 1000;
    private final FirebaseUtils firebaseUtils = FirebaseUtils.getInstance();

    private RecyclerView recyclerView;
    private TextView noLikedTracksTextView;
    private ImageView imageViewCat;
    private ProgressBar progressBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;

    private FirebaseRecyclerAdapter adapter;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public LikedFragment() {
    }

    public static LikedFragment newInstance() {
        return new LikedFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TracksActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_liked);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liked, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.liked_fragment_recycler_view);
        noLikedTracksTextView = (TextView) view.findViewById(R.id.liked_fragment_textview_no_liked);
        imageViewCat = (ImageView) view.findViewById(R.id.liked_fragment_image_no_liked);
        progressBar = (ProgressBar) view.findViewById(R.id.liked_fragment_progress_bar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.liked_fragment_collapsing_toolbar);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.liked_fragment_appbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setScrollingEnabled(true);
        hideNoTracks();

        Query query = firebaseUtils.getFirebaseDatabase()
                .child(TRACKS)
                .child(user.getUid())
                .orderByChild(IS_FAVORIRE)
                .equalTo(true);

        adapter = new FirebaseRecyclerAdapter<TracksModel, FitnessDataHolder>(TracksModel.class, R.layout.item_tracks_adapter, FitnessDataHolder.class, query) {
            @Override
            protected void populateViewHolder(final FitnessDataHolder viewHolder, TracksModel model, int position) {
                showLoading(false);
                viewHolder.setData(model, getContext());
            }

            @Override
            public FitnessDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                showLoading(true);
                FitnessDataHolder holder = super.onCreateViewHolder(parent, viewType);

                holder.setOnClickListener(new FitnessDataHolder.ClickListener() {
                    @Override
                    public void onClickItem(int position) {
                        TracksModel tracksModel = (TracksModel) adapter.getItem(position);

                        if (tracksModel.getTime() == 0) {
                            startActivity(new IntentBuilder()
                                    .context(getActivity())
                                    .activity(FitActivity.class)
                                    .extra(Constants.Extras.KEY_TYPE_FIT, tracksModel.getTypeFit())
                                    .build());
                        } else {
                            TrackMapActivity.Companion.start(getActivity(), tracksModel);
                        }
                    }

                    @Override
                    public void onFavoriteClick(int position) {
                        TracksModel adapterItem = (TracksModel) adapter.getItem(position);
                        adapterItem.setFavorite(!adapterItem.isFavorite());
                        firebaseUtils.updateFavorite(adapterItem, getActivity());
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
                                        firebaseUtils.removeTrack((TracksModel) adapter.getItem(position), getContext());
                                    }
                                })
                                .show();
                    }
                });
                return holder;
            }
        };

        firebaseUtils
                .getFirebaseDatabase()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        showLoading(false);
                        if (adapter.getItemCount() == 0) {
                            showNoTracks();
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

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (adapter.getItemCount() == 0) {
                    showNoTracks();
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
        setScrollingEnabled(false);
        noLikedTracksTextView.setText(getString(R.string.liked_fragment_no_more_liked_tracks) + "" + getEmojiByUnicode(UNICODE_SAD_CAT));
        noLikedTracksTextView.setVisibility(View.VISIBLE);
        animateView();
    }

    private void hideNoTracks() {
        imageViewCat.setVisibility(View.GONE);
        noLikedTracksTextView.setVisibility(View.GONE);
    }

    private void animateView() {
        Animation scale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(DURATION);
        imageViewCat.setVisibility(View.VISIBLE);
        imageViewCat.setAnimation(scale);

        Animation movingText = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.ZORDER_TOP, -5, Animation.RELATIVE_TO_SELF, 0);
        movingText.setDuration(DURATION);
        noLikedTracksTextView.setAnimation(movingText);
    }

    private void setScrollingEnabled(boolean isEnabled) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
        if (isEnabled) {
            params.setScrollFlags((AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS));
            collapsingToolbarLayout.setVisibility(View.VISIBLE);
            appBarLayout.setExpanded(true, true);
        } else {
            appBarLayout.setExpanded(false, true);
            params.setScrollFlags(0);
            collapsingToolbarLayout.setVisibility(View.GONE);
        }
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
}
