package com.egoriku.catsrunning.extensions

import android.annotation.SuppressLint
import android.content.Context
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.helpers.TypeFit
import com.egoriku.core_lib.extensions.drawableCompat

@SuppressLint("SwitchIntDef")
fun drawableTypeFit(context: Context, @TypeFit typeFit: Int) = when (typeFit) {
    TypeFit.WALKING -> drawableCompat(context, R.drawable.ic_vec_directions_walk_40dp)
    TypeFit.RUNNING -> drawableCompat(context, R.drawable.ic_vec_directions_run_40dp)
    TypeFit.CYCLING -> drawableCompat(context, R.drawable.ic_vec_directions_cycling_40dp)
    else -> drawableCompat(context, R.drawable.ic_vec_black_cat)
}

@SuppressLint("SwitchIntDef")
fun drawableTypeReminder(context: Context, @TypeFit typeFit: Int) = when (typeFit) {
    TypeFit.WALKING -> drawableCompat(context, R.drawable.ic_vec_directions_walk_reminders)
    TypeFit.RUNNING -> drawableCompat(context, R.drawable.ic_vec_directions_run_reminders)
    TypeFit.CYCLING -> drawableCompat(context, R.drawable.ic_vec_directions_bike_reminders)
    else -> drawableCompat(context, R.drawable.ic_vec_black_cat)
}

