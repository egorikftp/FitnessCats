package com.egoriku.catsrunning.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.egoriku.catsrunning.models.Firebase.SaveModel;
import com.egoriku.catsrunning.models.SpinnerIntervalModel;
import com.egoriku.catsrunning.ui.statisticChart.FitChart;
import com.egoriku.catsrunning.ui.statisticChart.FitChartValue;
import com.egoriku.catsrunning.utils.CustomFont;
import com.egoriku.catsrunning.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static com.egoriku.catsrunning.models.Constants.FirebaseFields.BEGINS_AT;
import static com.egoriku.catsrunning.models.Constants.FirebaseFields.TRACKS;

public class StatisticFragment extends Fragment {
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
    private TextView noDataView;
    private ImageView imageWalkView;
    private ImageView imageRunningView;
    private ImageView imageCyclingView;
    private Spinner spinner;
    private boolean isHide;
    private View[] icons;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final Calendar calendar = Calendar.getInstance();


    private Map<Integer, List<SaveModel>> statisticValues;

    int[] colorResId = {R.color.chart_value_first_color, R.color.chart_value_second_color, R.color.chart_value_third_color};

    public StatisticFragment() {
    }

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }


    @Override
    public void onStart() {
        super.onStart();
        ((TracksActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_statistic, FragmentsTag.STATISTIC);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        fitChart = (FitChart) view.findViewById(R.id.fit_chart);
        allDistanceView = (TextView) view.findViewById(R.id.statistic_fragment_all_distance_view);
        fitResultView = (TextView) view.findViewById(R.id.statistic_fragment_fit_result_view);
        noDataView = (TextView) view.findViewById(R.id.statistic_fragment_txt_no_data);
        imageWalkView = (ImageView) view.findViewById(R.id.statistic_fragment_ic_walk);
        imageRunningView = (ImageView) view.findViewById(R.id.statistic_fragment_ic_run);
        imageCyclingView = (ImageView) view.findViewById(R.id.statistic_fragment_ic_cycling);
        spinner = (Spinner) getActivity().findViewById(R.id.spinner_nav);
        rootLayout = (RelativeLayout) view.findViewById(R.id.statistic_fragment_root_no_data);
        rootFrame = (FrameLayout) view.findViewById(R.id.statistic_fragment_root_chart);

        noDataView.setTypeface(CustomFont.getTypeFace());
        addItemsToSpinner();

        icons = new View[]{imageWalkView, imageRunningView, imageCyclingView};
        return view;
    }

    private void getTracksFromInterval(long valueInterval) {
        long startDate = (calendar.getTimeInMillis() - valueInterval) / 1000L;

        FirebaseUtils.getDatabaseReference()
                .child(TRACKS)
                .child(user.getUid())
                .orderByChild(BEGINS_AT)
                .startAt(startDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        statisticValues = new HashMap<>();

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            SaveModel saveModel = child.getValue(SaveModel.class);

                            List<SaveModel> modelList = statisticValues.get(saveModel.getTypeFit());
                            if (modelList == null) {
                                modelList = new ArrayList<>();
                            }

                            modelList.add(saveModel);
                            statisticValues.put(saveModel.getTypeFit(), modelList);
                        }
                        onLoadFinished();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.d(databaseError.getMessage());
                    }
                });
    }

    private void onLoadFinished() {
        int maxValue = getMaxDistance();
        int resultDistance = getCountDistance();

        if (maxValue == 0 && resultDistance == 0) {
            animateViewShow();
            rootFrame.setVisibility(View.GONE);
        } else {
            animateViewHide();
            rootFrame.setVisibility(View.VISIBLE);
            setUpFitChart(maxValue, resultDistance);
        }
    }

    private void addItemsToSpinner() {
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
                getTracksFromInterval(intervals.get(position).getValueInterval());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                Timber.d("onNothingSelected");
            }
        });
    }

    @SuppressLint("WrongConstant")
    private long getCurrentDayTime() {
        Calendar calendar = Calendar.getInstance();
        long currentMinutes = calendar.get(Calendar.MINUTE);
        long currentHours = calendar.get(Calendar.HOUR_OF_DAY);
        return currentHours * HOURS_IN_SEC + currentMinutes * MINUTES_IN_SEC;
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

    private void setUpFitChart(int maxValue, int allDistance) {
        setUpTextView(allDistance);

        fitChart.setMinValue(0);
        fitChart.setMaxValue(maxValue * 2);

        Collection<FitChartValue> values = new ArrayList<>();

        int i = 0;
        for (Map.Entry<Integer, List<SaveModel>> value : statisticValues.entrySet()) {
            int distance = 0;
            List<SaveModel> saveModels = value.getValue();

            for (int j = 0; j < saveModels.size(); j++) {
                distance += saveModels.get(j).getDistance();
            }
            if (distance != 0) {
                values.add(new FitChartValue(distance, ContextCompat.getColor(getContext(), colorResId[value.getKey() - 1])));
                icons[value.getKey() - 1].setVisibility(View.VISIBLE);
            } else {
                values.add(new FitChartValue(distance, ContextCompat.getColor(getContext(), R.color.chart_value_empty_color)));
                icons[value.getKey() - 1].setVisibility(View.INVISIBLE);
            }
            i++;
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

    private int getCountDistance() {
        int allDistance = 0;
        for (Map.Entry<Integer, List<SaveModel>> value : statisticValues.entrySet()) {
            for (int i = 0; i < value.getValue().size(); i++) {
                allDistance += value.getValue().get(i).getDistance();
            }
        }
        return allDistance;
    }

    private int getMaxDistance() {
        int maxValue = 0;
        for (Map.Entry<Integer, List<SaveModel>> value : statisticValues.entrySet()) {
            for (int i = 0; i < value.getValue().size(); i++) {
                int distance = value.getValue().get(i).getDistance();
                if (distance > maxValue) {
                    maxValue = distance;
                }
            }
        }
        return maxValue;
    }

    @Override
    public void onStop() {
        super.onStop();
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        spinner.setVisibility(View.VISIBLE);
    }
}
