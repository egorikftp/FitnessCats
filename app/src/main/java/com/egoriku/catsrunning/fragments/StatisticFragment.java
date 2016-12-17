package com.egoriku.catsrunning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.egoriku.catsrunning.adapters.CustomSpinnerAdapter;
import com.egoriku.catsrunning.loaders.AsyncStatisticLoader;
import com.egoriku.catsrunning.models.SpinnerIntervalModel;
import com.egoriku.catsrunning.models.StatisticModel;
import com.egoriku.catsrunning.ui.statisticChart.FitChart;
import com.egoriku.catsrunning.ui.statisticChart.FitChartValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static com.egoriku.catsrunning.models.Constants.Extras.KEY_BUNDLE_TIME_AMOUNT;
import static com.egoriku.catsrunning.models.Constants.Tags.TAG_STATISTIC_FRAGMENT;

public class StatisticFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<StatisticModel>> {
    private static final int ID_LOADER = 1;
    public static final int FIT_COMPARE_DISTANCE = 1000;
    public static final int DURATION_HIDE = 500;
    public static final int DURATION_SHOW = 1000;
    public static final int HOURS_IN_SEC = 3600;
    public static final int MINUTES_IN_SEC = 60;
    private RelativeLayout rootLayout;
    private FrameLayout rootFrame;
    private FitChart fitChart;
    private TextView allDistanceView;
    private TextView fitResultView;
    private ImageView imageWalkView;
    private ImageView imageRunningView;
    private ImageView imageCyclingView;
    private Spinner spinner;
    private boolean isHide;
    private View[] icons;

    int[] colorResId = {R.color.chart_value_first_color, R.color.chart_value_second_color, R.color.chart_value_third_color};


    public StatisticFragment() {
    }


    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }


    @Override
    public void onStart() {
        super.onStart();
        ((TracksActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_statistic, TAG_STATISTIC_FRAGMENT);
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        fitChart = (FitChart) view.findViewById(R.id.fit_chart);
        allDistanceView = (TextView) view.findViewById(R.id.statistic_fragment_all_distance_view);
        fitResultView = (TextView) view.findViewById(R.id.statistic_fragment_fit_result_view);
        imageWalkView = (ImageView) view.findViewById(R.id.statistic_fragment_ic_walk);
        imageRunningView = (ImageView) view.findViewById(R.id.statistic_fragment_ic_run);
        imageCyclingView = (ImageView) view.findViewById(R.id.statistic_fragment_ic_cycling);
        spinner = (Spinner) getActivity().findViewById(R.id.spinner_nav);
        rootLayout = (RelativeLayout) view.findViewById(R.id.statistic_fragment_root_no_data);
        rootFrame = (FrameLayout) view.findViewById(R.id.statistic_fragment_root_chart);
        spinner.setVisibility(View.VISIBLE);

        addItemsToSpinner();

        icons = new View[]{imageWalkView, imageRunningView, imageCyclingView};
        return view;
    }


    private void initLoader(long valueInterval) {
        Calendar calendar = Calendar.getInstance();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_BUNDLE_TIME_AMOUNT, (calendar.getTimeInMillis() - valueInterval) / 1000L);
        getLoaderManager().restartLoader(ID_LOADER, bundle, this);
    }


    public void addItemsToSpinner() {
        final List<SpinnerIntervalModel> intervals = new ArrayList<>();
        intervals.add(new SpinnerIntervalModel(getString(R.string.spinner_day), getCurrentDayTime()));
        intervals.add(new SpinnerIntervalModel(getString(R.string.spinner_week), DateUtils.WEEK_IN_MILLIS));
        intervals.add(new SpinnerIntervalModel(getString(R.string.spinner_month), DateUtils.WEEK_IN_MILLIS * 4));
        intervals.add(new SpinnerIntervalModel(getString(R.string.spinner_quarter), DateUtils.WEEK_IN_MILLIS * 12));
        intervals.add(new SpinnerIntervalModel(getString(R.string.spinner_half_year), DateUtils.WEEK_IN_MILLIS * 24));
        intervals.add(new SpinnerIntervalModel(getString(R.string.spinner_year), DateUtils.YEAR_IN_MILLIS));

        spinner.setAdapter(new CustomSpinnerAdapter(getContext(), intervals));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                initLoader(intervals.get(position).getValueInterval());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


    private long getCurrentDayTime() {
        Calendar calendar = Calendar.getInstance();
        long currentMinutes = calendar.get(Calendar.MINUTE);
        long currentHours = calendar.get(Calendar.HOUR_OF_DAY);
        return currentHours * HOURS_IN_SEC + currentMinutes * MINUTES_IN_SEC;
    }


    @Override
    public Loader<List<StatisticModel>> onCreateLoader(int id, Bundle args) {
        return new AsyncStatisticLoader(getContext(), args);
    }


    @Override
    public void onLoadFinished(Loader<List<StatisticModel>> loader, List<StatisticModel> data) {
        int maxValue = getMaxDistance(data);
        int allDistance = getCountDistance(data);

        if (maxValue == 0 && allDistance == 0) {
            animateViewShow();
            rootFrame.setVisibility(View.GONE);
        } else {
            animateViewHide();
            rootFrame.setVisibility(View.VISIBLE);
            setUpFitChart(data, maxValue, allDistance);
        }
    }

    private void animateViewShow() {
        rootLayout.setVisibility(View.VISIBLE);
        Animation movingText = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.ZORDER_TOP, 2, Animation.RELATIVE_TO_SELF, 0);
        movingText.setDuration(DURATION_SHOW);
        rootLayout.setAnimation(movingText);
        isHide = false;
    }


    private void animateViewHide() {
        if (!isHide) {
            Animation movingText = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.ZORDER_TOP, 0, Animation.RELATIVE_TO_SELF, 2);
            movingText.setDuration(DURATION_HIDE);
            rootLayout.setAnimation(movingText);
            rootLayout.setVisibility(View.GONE);
        }
        isHide = true;
    }


    private void setUpFitChart(List<StatisticModel> data, int maxValue, int allDistance) {
        setUpTextView(allDistance);

        fitChart.setMinValue(0);
        fitChart.setMaxValue(maxValue * 2);

        Collection<FitChartValue> values = new ArrayList<>();
        for (int i = 0; i <= 2; i++) {
            if (!(data.get(i).getFitDistance() == 0)) {
                values.add(new FitChartValue(data.get(i).getFitDistance(), getResources().getColor(colorResId[i])));
                icons[i].setVisibility(View.VISIBLE);
            } else {
                values.add(new FitChartValue(data.get(i).getFitDistance(), getResources().getColor(R.color.chart_value_empty_color)));
                icons[i].setVisibility(View.INVISIBLE);
            }
        }
        fitChart.setValues(values);
    }


    private void setUpTextView(int allDistance) {
        allDistanceView.setText(String.format(getString(R.string.statistic_fragment_result), allDistance));

        if (allDistance > FIT_COMPARE_DISTANCE) {
            fitResultView.setText(getString(R.string.statistic_fragment_good_fit));
        } else {
            fitResultView.setText(getString(R.string.statistic_fragment_bad_fit));
        }
    }


    private int getCountDistance(List<StatisticModel> data) {
        int allDistance = 0;
        for (StatisticModel model : data) {
            allDistance += model.getFitDistance();
        }
        return allDistance;
    }


    private int getMaxDistance(List<StatisticModel> data) {
        int maxValue = 0;
        for (StatisticModel statisticModel : data) {
            if (statisticModel.getFitDistance() > maxValue) {
                maxValue = statisticModel.getFitDistance();
            }
        }
        return maxValue;
    }

    @Override
    public void onLoaderReset(Loader<List<StatisticModel>> loader) {
    }


    @Override
    public void onStop() {
        super.onStop();
        spinner.setVisibility(View.GONE);
    }
}
