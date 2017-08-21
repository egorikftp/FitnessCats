package com.egoriku.catsrunning.utils;


import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;

import java.util.Arrays;

public class TypeFitBuilder {

    public static String getTypeFit(int typeFit, boolean isLowCase, int resId) {
        return isLowCase ? Arrays.asList(App.getInstance().getResources().getStringArray(resId)).get(typeFit - 1).toLowerCase() : Arrays.asList(App.getInstance().getResources().getStringArray(R.array.type_reminder)).get(typeFit - 1);
    }
}
