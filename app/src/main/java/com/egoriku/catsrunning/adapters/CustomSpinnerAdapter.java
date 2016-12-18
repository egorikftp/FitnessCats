package com.egoriku.catsrunning.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.models.SpinnerIntervalModel;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<SpinnerIntervalModel> {
    private List<SpinnerIntervalModel> intervals;


    public CustomSpinnerAdapter(Context context, List<SpinnerIntervalModel> intervals) {
        super(context, R.layout.item_spinner, intervals);
        this.intervals = intervals;
    }


    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    @NonNull
    private View getCustomView(int position, View convertView, ViewGroup parent) {
        ViewGroup viewGroup;
        ViewHolder viewHolder;

        if (convertView != null) {
            viewGroup = (ViewGroup) convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewGroup = (ViewGroup) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_spinner, parent, false);
            viewHolder = new ViewHolder(viewGroup);
            viewGroup.setTag(viewHolder);
        }

        viewHolder.someInterval.setText(intervals.get(position).getNameInterval());
        return viewGroup;
    }


    private static class ViewHolder {
        private TextView someInterval;

        private ViewHolder(ViewGroup viewGroup) {
            someInterval = (TextView) viewGroup.findViewById(R.id.item_interval);
        }
    }
}
