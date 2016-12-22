package com.egoriku.catsrunning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import com.egoriku.catsrunning.adapters.LikedFragmentAdapter;
import com.egoriku.catsrunning.adapters.interfaces.ILikedClickListener;
import com.egoriku.catsrunning.loaders.AsyncLikedTracksLoader;
import com.egoriku.catsrunning.models.AllFitnessDataModel;
import com.egoriku.catsrunning.utils.CustomFont;
import com.egoriku.catsrunning.utils.IntentBuilder;

import java.util.List;

import static com.egoriku.catsrunning.helpers.DbActions.updateLikedDigit;
import static com.egoriku.catsrunning.models.Constants.Tags.TAG_LIKED_FRAGMENT;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_DISTANCE;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_ID;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_LIKED;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_TIME_RUNNING;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_TOKEN;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_TYPE_FIT;

public class LikedFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<AllFitnessDataModel>> {
    private static final int UNICODE_SAD_CAT = 0x1F640;
    public static final int DURATION = 1000;
    public static final int ID_LOADER = 1;
    private RecyclerView recyclerView;
    private TextView noLikedTracksTextView;
    private ImageView imageViewCat;
    private ProgressBar progressBar;
    private LikedFragmentAdapter likedFragmentAdapter;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;


    public LikedFragment() {
    }


    public static LikedFragment newInstance() {
        return new LikedFragment();
    }


    @Override
    public void onStart() {
        super.onStart();
        ((TracksActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_liked, TAG_LIKED_FRAGMENT);
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

        likedFragmentAdapter = new LikedFragmentAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getInstance()));
        recyclerView.hasFixedSize();

        noLikedTracksTextView.setText(null);
        noLikedTracksTextView.setTypeface(CustomFont.getTypeFace());
        imageViewCat.setVisibility(View.GONE);
        return view;
    }


    private void setUpAdapter(final List<AllFitnessDataModel> data) {
        progressBar.setVisibility(View.GONE);

        if (data.size() == 0) {
            setScrollingEnabled(false);
            noLikedTracksTextView.setText(getString(R.string.liked_fragment_no_more_liked_tracks) + "" + getEmojiByUnicode(UNICODE_SAD_CAT));
            animateView();
        } else {
            setScrollingEnabled(true);
            imageViewCat.setVisibility(View.GONE);
            likedFragmentAdapter.setData(data);
            recyclerView.setAdapter(likedFragmentAdapter);

            likedFragmentAdapter.setOnItemClickListener(new ILikedClickListener() {
                @Override
                public void onItemClick(AllFitnessDataModel item, int position) {
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
                    int likedDigit = 0;
                    updateLikedDigit(likedDigit, data.get(position).getId());
                    data.remove(position);
                    likedFragmentAdapter.notifyItemRemoved(position);
                    likedFragmentAdapter.notifyItemRangeChanged(position, data.size());

                    if (data.size() == 0) {
                        Snackbar.make(recyclerView, R.string.liked_fragment_snackbar_liked_empty, Snackbar.LENGTH_LONG).show();
                        setUpAdapter(data);
                    }
                }
            });
        }
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


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(ID_LOADER, null, this);
    }


    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }


    @Override
    public Loader<List<AllFitnessDataModel>> onCreateLoader(int id, Bundle args) {
        return new AsyncLikedTracksLoader(getContext());
    }


    @Override
    public void onLoadFinished(Loader<List<AllFitnessDataModel>> loader, List<AllFitnessDataModel> data) {
        setUpAdapter(data);
    }


    @Override
    public void onLoaderReset(Loader<List<AllFitnessDataModel>> loader) {

    }
}
