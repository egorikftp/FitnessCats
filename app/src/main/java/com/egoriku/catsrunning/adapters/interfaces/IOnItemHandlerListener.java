package com.egoriku.catsrunning.adapters.interfaces;

import com.egoriku.catsrunning.models.AllFitnessDataModel;

public interface IOnItemHandlerListener {
    void onClickItem(AllFitnessDataModel item, int position);
    void onLikedClick(AllFitnessDataModel item, int position);
    void onLongClick(AllFitnessDataModel item, int position);
}
