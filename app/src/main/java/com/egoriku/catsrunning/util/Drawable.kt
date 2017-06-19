package com.egoriku.catsrunning.util

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.v7.content.res.AppCompatResources
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.helpers.TypeFit


fun drawableCompat(context: Context, @DrawableRes resourceId: Int) = AppCompatResources.getDrawable(context, resourceId)

fun drawableTypeFit(context: Context, @TypeFit typeFit: Int) = when (typeFit) {
    TypeFit.WALKING -> drawableCompat(context, R.drawable.ic_vec_directions_walk_40dp)
    TypeFit.RUNNING -> drawableCompat(context, R.drawable.ic_vec_directions_run_40dp)
    TypeFit.CYCLING -> drawableCompat(context, R.drawable.ic_vec_directions_bike_40dp)
    else -> drawableCompat(context, R.drawable.ic_vec_black_cat)
}

fun drawableTypeReminder(context: Context, @TypeFit typeFit: Int) = when (typeFit) {
    TypeFit.WALKING -> drawableCompat(context, R.drawable.ic_vec_directions_walk_reminders)
    TypeFit.RUNNING -> drawableCompat(context, R.drawable.ic_vec_directions_run_reminders)
    TypeFit.CYCLING -> drawableCompat(context, R.drawable.ic_vec_directions_bike_reminders)
    else -> drawableCompat(context, R.drawable.ic_vec_black_cat)
}

