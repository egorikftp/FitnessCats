package com.egoriku.catsrunning.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.FitActivity;
import com.egoriku.catsrunning.activities.TrackOnMapsActivity;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.egoriku.catsrunning.adapters.FitnessDataHolder;
import com.egoriku.catsrunning.models.Constants;
import com.egoriku.catsrunning.models.Firebase.SaveModel;
import com.egoriku.catsrunning.utils.CustomFont;
import com.egoriku.catsrunning.utils.IntentBuilder;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import timber.log.Timber;

import static com.egoriku.catsrunning.models.Constants.FirebaseFields.CHILD_TRACKS;
import static com.egoriku.catsrunning.models.Constants.FirebaseFields.IS_FAVORIRE;

public class LikedFragment extends Fragment {

    private static final int UNICODE_SAD_CAT = 0x1F640;
    private static final int DURATION = 1000;

    private RecyclerView recyclerView;
    private TextView noLikedTracksTextView;
    private ImageView imageViewCat;
    private ProgressBar progressBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;

    private FirebaseRecyclerAdapter adapter;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public LikedFragment() {
    }

    public static LikedFragment newInstance() {
        return new LikedFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TracksActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_liked, FragmentsTag.LIKED);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liked, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.liked_fragment_recycler_view);
        noLikedTracksTextView = (TextView) view.findViewById(R.id.liked_fragment_textview_no_liked);
        imageViewCat = (ImageView) view.findViewById(R.id.liked_fragment_image_no_liked);
        progressBar = (ProgressBar) view.findViewById(R.id.liked_fragment_progress_bar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.liked_fragment_collapsing_toolbar);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.liked_fragment_appbar);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getInstance()));
        recyclerView.hasFixedSize();

        noLikedTracksTextView.setTypeface(CustomFont.getTypeFace());
        hideNoTracks();

        Query query = databaseReference
                .child(CHILD_TRACKS)
                .child(user.getUid())
                .orderByChild(IS_FAVORIRE)
                .equalTo(true);

        adapter = new FirebaseRecyclerAdapter<SaveModel, FitnessDataHolder >(SaveModel.class, R.layout.adapter_fitness_data_fragment, FitnessDataHolder.class, query) {
            @Override
            protected void populateViewHolder(final FitnessDataHolder viewHolder, SaveModel model, int position) {
                Timber.d("populate");
                progressBar.setVisibility(View.GONE);
                viewHolder.setData(model, getContext());
            }

            @Override
            public int getItemCount() {
                int itemCount = super.getItemCount();
                Timber.d("getItemCount " + itemCount);
               /* if (itemCount == 0) {
                    showNoTracks();
                }else {
                    hideNoTracks();
                }*/
                return itemCount;
            }

            @Override
            protected void onChildChanged(ChangeEventListener.EventType type, int index, int oldIndex) {
                super.onChildChanged(type, index, oldIndex);
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
                                    .extra(Constants.Extras.KEY_TYPE_FIT, saveModel.getTypeFit())
                                    .build());
                        } else {
                            TrackOnMapsActivity.start(getActivity(), saveModel);
                        }
                    }

                    @Override
                    public void onFavoriteClick(int position) {
                        SaveModel adapterItem = (SaveModel) adapter.getItem(position);
                        adapterItem.setFavorite(!adapterItem.isFavorite());
                        //updateTrackFavorire(adapterItem);
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
                                        //removeTrackFromFirebase((SaveModel) adapter.getItem(position));

                                    }
                                })
                                .show();
                    }
                });
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        return view;
    }

    private void showNoTracks() {
        setScrollingEnabled(false);
        noLikedTracksTextView.setText(getString(R.string.liked_fragment_no_more_liked_tracks) + "" + getEmojiByUnicode(UNICODE_SAD_CAT));
        noLikedTracksTextView.setVisibility(View.VISIBLE);
        animateView();
    }

    private void hideNoTracks() {
        setScrollingEnabled(true);
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
            appBarLayout.setExpanded(false, false);
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
