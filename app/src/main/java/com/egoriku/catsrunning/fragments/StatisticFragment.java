package com.egoriku.catsrunning.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.egoriku.catsrunning.ui.statisticChart.FitChart;
import com.egoriku.catsrunning.ui.statisticChart.FitChartValue;

import java.util.ArrayList;
import java.util.Collection;

import static com.egoriku.catsrunning.models.Constants.Tags.TAG_STATISTIC_FRAGMENT;

public class StatisticFragment extends Fragment {
    private FitChart fitChart;
    private Button button;


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
        fitChart = (FitChart) view.findViewById(R.id.fitChart);
        button = (Button) view.findViewById(R.id.add_create_statistic);

        fitChart.setMinValue(0f);
        fitChart.setMaxValue(8000f);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources resources = getResources();

                Collection<FitChartValue> values = new ArrayList<>();
                values.add(new FitChartValue(4169f, resources.getColor(R.color.chart_value_3)));
                values.add(new FitChartValue(2796f, resources.getColor(R.color.chart_value_1)));
                values.add(new FitChartValue(3910f, resources.getColor(R.color.chart_value_2)));
                fitChart.setValues(values);
            }
        });

        return view;
    }
}
