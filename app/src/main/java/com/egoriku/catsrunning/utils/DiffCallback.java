package com.egoriku.catsrunning.utils;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.egoriku.catsrunning.data.commons.TracksModel;

import java.util.List;

public class DiffCallback extends DiffUtil.Callback {

    private List<TracksModel> newList;
    private List<TracksModel> oldList;

    public DiffCallback(List<TracksModel> newList, List<TracksModel> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getTrackToken().equals(newList.get(newItemPosition).getTrackToken());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == (newList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
