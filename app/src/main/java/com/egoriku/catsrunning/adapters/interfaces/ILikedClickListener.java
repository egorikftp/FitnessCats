package com.egoriku.catsrunning.adapters.interfaces;

import com.egoriku.catsrunning.models.AllFitnessDataModel;

public interface ILikedClickListener {
    void onItemClick(AllFitnessDataModel item, int position);
    void onLikedClick(AllFitnessDataModel item, int position);
}
