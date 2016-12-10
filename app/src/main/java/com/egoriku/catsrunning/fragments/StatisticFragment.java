package com.egoriku.catsrunning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.egoriku.catsrunning.loaders.AsyncStatisticLoader;
import com.egoriku.catsrunning.models.StatisticModel;
import com.egoriku.catsrunning.ui.statisticChart.FitChart;
import com.egoriku.catsrunning.ui.statisticChart.FitChartValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.egoriku.catsrunning.models.Constants.Tags.TAG_STATISTIC_FRAGMENT;

public class StatisticFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<StatisticModel>> {
    private static final int ID_LOADER = 1;
    private FitChart fitChart;
    private TextView allDistanceView;


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
        Calendar calendar = Calendar.getInstance();
        Log.e("time", String.valueOf(calendar.getTimeInMillis() / 1000));
        Log.e("time", String.valueOf((calendar.getTimeInMillis() - DateUtils.WEEK_IN_MILLIS) / 1000));

        getLoaderManager().initLoader(ID_LOADER, null, this);
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        fitChart = (FitChart) view.findViewById(R.id.fitChart);
        allDistanceView = (TextView) view.findViewById(R.id.statistic_all_distance);
        return view;
    }

    @Override
    public Loader<List<StatisticModel>> onCreateLoader(int id, Bundle args) {
        return new AsyncStatisticLoader(getContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<StatisticModel>> loader, List<StatisticModel> data) {
        float maxValue = getMax(data);
        fitChart.setMinValue(0f);
        fitChart.setMaxValue(maxValue * 2);

        allDistanceView.setText((int)maxValue + " метров");

        Collection<FitChartValue> values = new ArrayList<>();
        values.add(new FitChartValue(data.get(0).getFitDistance(), getResources().getColor(R.color.chart_value_3)));
        values.add(new FitChartValue(data.get(0).getFitDistance() - 10, getResources().getColor(R.color.chart_value_1)));
        //values.add(new FitChartValue(3910, getResources().getColor(R.color.chart_value_2)));
        fitChart.setValues(values);
    }

    private float getMax(List<StatisticModel> data) {
        return Collections.max(data, null).getFitDistance();
    }

    @Override
    public void onLoaderReset(Loader<List<StatisticModel>> loader) {

    }
}
