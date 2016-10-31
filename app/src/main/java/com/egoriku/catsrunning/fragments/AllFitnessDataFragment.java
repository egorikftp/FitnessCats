package com.egoriku.catsrunning.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.TracksActivity;

import static com.egoriku.catsrunning.utils.VectorToDrawable.getDrawable;


public class AllFitnessDataFragment extends Fragment {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SparseArray<String> sparseTabs;

    public AllFitnessDataFragment() {
    }

    public static AllFitnessDataFragment newInstance() {
        return new AllFitnessDataFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_fitness_data, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.container);

        sparseTabs = new SparseArray<>();
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
                Log.d("pos", String.valueOf(position));
                ((TracksActivity) getActivity()).tabTitle(sparseTabs.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }


    private void initSparseTabs() {
        sparseTabs.put(0, getString(R.string.tab_text_walking));
        sparseTabs.put(1, getString(R.string.tab_text_running));
        sparseTabs.put(2, getString(R.string.tab_text_cycling));
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FitnessDataFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return sparseTabs.size();
        }
    }
}
