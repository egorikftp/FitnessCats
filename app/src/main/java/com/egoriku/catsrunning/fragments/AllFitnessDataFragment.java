package com.egoriku.catsrunning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.FitActivity;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.egoriku.catsrunning.models.TypeFit;

import static com.egoriku.catsrunning.utils.VectorToDrawable.getDrawable;

public class AllFitnessDataFragment extends Fragment {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SparseArray<String> sparseTabs;

    private FloatingActionButton fabMain;
    private FloatingActionButton fabWalk;
    private FloatingActionButton fabCycling;
    private FloatingActionButton fabRun;
    private Animation fabWalkShow;
    private Animation fabCyclingShow;
    private Animation fabRunShow;
    private Animation fabWalkHide;
    private Animation fabCyclingHide;
    private Animation fabRunHide;
    private boolean fabStatus;
    private AppBarLayout appBarLayout;


    public AllFitnessDataFragment() {
    }


    public static AllFitnessDataFragment newInstance() {
        return new AllFitnessDataFragment();
    }


    @Override
    public void onStart() {
        super.onStart();
        ((TracksActivity) getActivity()).onFragmentStart(R.string.tab_text_walking, FragmentsTag.MAIN);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_fitness_data, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.fragment_all_fitness_data_view_pager_container);

        appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        fabMain = (FloatingActionButton) view.findViewById(R.id.floating_button);
        fabWalk = (FloatingActionButton) view.findViewById(R.id.fab_walk);
        fabCycling = (FloatingActionButton) view.findViewById(R.id.fab_cycling);
        fabRun = (FloatingActionButton) view.findViewById(R.id.fab_run);

        fabWalkShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_walk_show);
        fabWalkHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_walk_hide);
        fabRunShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_run_show);
        fabRunHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_run_hide);
        fabCyclingShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_cycling_show);
        fabCyclingHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_cycling_hide);
        fabStatus = false;

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabStatus) {
                    changeFabState(fabStatus);
                    fabStatus = false;
                } else {
                    changeFabState(fabStatus);
                    fabStatus = true;
                }
            }
        });

        fabWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FitActivity.start(getActivity(), TypeFit.WALKING);
            }
        });


        fabRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FitActivity.start(getActivity(), TypeFit.RUNNING);
            }
        });

        fabCycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FitActivity.start(getActivity(), TypeFit.CYCLING);
            }
        });

        int[] imageResId = {
                R.drawable.ic_vec_directions_walk_white_24dp,
                R.drawable.ic_vec_directions_run_white_24dp,
                R.drawable.ic_vec_directions_bike_white_24dp
        };

        initSparseTabs();
        sectionsPagerAdapter = new AllFitnessDataFragment.SectionsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(getDrawable(imageResId[i]));
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }


            @Override
            public void onPageSelected(int position) {
                ((TracksActivity) getActivity()).tabTitle(sparseTabs.get(position));
            }


            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return view;
    }


    private void initSparseTabs() {
        sparseTabs = new SparseArray<>();
        sparseTabs.put(0, getString(R.string.tab_text_walking));
        sparseTabs.put(1, getString(R.string.tab_text_running));
        sparseTabs.put(2, getString(R.string.tab_text_cycling));
    }


    @Override
    public void onResume() {
        super.onResume();
        fabStatus = false;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (fabStatus) {
            changeFabState(true);
        }
    }


    private void changeFabState(boolean status) {
        FrameLayout.LayoutParams layoutParamsFabRun = (FrameLayout.LayoutParams) fabRun.getLayoutParams();
        FrameLayout.LayoutParams layoutParamsFabWalk = (FrameLayout.LayoutParams) fabWalk.getLayoutParams();
        FrameLayout.LayoutParams layoutParamsFabCycling = (FrameLayout.LayoutParams) fabCycling.getLayoutParams();

        if (status) {
            layoutParamsFabWalk.rightMargin -= (int) (fabWalk.getWidth() * 1.4);
            layoutParamsFabWalk.bottomMargin -= (int) (fabWalk.getHeight() * 0.0);
            fabWalk.setLayoutParams(layoutParamsFabWalk);

            layoutParamsFabRun.rightMargin -= (int) (fabRun.getWidth() * 1.1);
            layoutParamsFabRun.bottomMargin -= (int) (fabRun.getHeight() * 1.1);
            fabRun.setLayoutParams(layoutParamsFabRun);

            layoutParamsFabCycling.rightMargin -= (int) (fabCycling.getWidth() * 0.0);
            layoutParamsFabCycling.bottomMargin -= (int) (fabCycling.getHeight() * 1.4);
            fabCycling.setLayoutParams(layoutParamsFabCycling);

            fabRun.setClickable(false);
            fabWalk.setClickable(false);
            fabCycling.setClickable(false);

            fabRun.startAnimation(fabRunHide);
            fabWalk.startAnimation(fabWalkHide);
            fabCycling.startAnimation(fabCyclingHide);
        } else {
            layoutParamsFabWalk.rightMargin += (int) (fabWalk.getWidth() * 1.4);
            layoutParamsFabWalk.bottomMargin += (int) (fabWalk.getHeight() * 0.0);
            fabWalk.setLayoutParams(layoutParamsFabWalk);

            layoutParamsFabRun.rightMargin += (int) (fabRun.getWidth() * 1.1);
            layoutParamsFabRun.bottomMargin += (int) (fabRun.getHeight() * 1.1);
            fabRun.setLayoutParams(layoutParamsFabRun);

            layoutParamsFabCycling.rightMargin += (int) (fabCycling.getWidth() * 0.0);
            layoutParamsFabCycling.bottomMargin += (int) (fabCycling.getHeight() * 1.4);
            fabCycling.setLayoutParams(layoutParamsFabCycling);

            fabRun.setClickable(true);
            fabWalk.setClickable(true);
            fabCycling.setClickable(true);

            fabRun.startAnimation(fabRunShow);
            fabWalk.startAnimation(fabWalkShow);
            fabCycling.startAnimation(fabCyclingShow);
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FitnessDataFragment.newInstance(position + 1, new IFABScroll() {
                @Override
                public void onScrollChange() {
                    if (fabStatus) {
                        changeFabState(fabStatus);
                        fabStatus = false;
                    }
                }
            });
        }

        @Override
        public int getCount() {
            return sparseTabs.size();
        }
    }
}
