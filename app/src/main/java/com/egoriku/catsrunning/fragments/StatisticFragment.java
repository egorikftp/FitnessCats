package com.egoriku.catsrunning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.TracksActivity;

public class StatisticFragment extends Fragment {
    public static final String TAG_STATISTIC_FRAGMENT = "TAG_STATISTIC_FRAGMENT";

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

        return view;
    }
}
