package com.egoriku.catsrunning.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun inflateViewGroup(viewGroup: ViewGroup, layoutId: Int, attachToRoot: Boolean = false): ViewGroup {
    return LayoutInflater.from(viewGroup.context).inflate(layoutId, viewGroup, attachToRoot) as ViewGroup
}
