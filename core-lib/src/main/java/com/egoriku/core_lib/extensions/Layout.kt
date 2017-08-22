@file:Suppress("NOTHING_TO_INLINE")

package com.egoriku.core_lib.extensions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

inline fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

inline fun inflateViewGroup(viewGroup: ViewGroup, layoutId: Int, attachToRoot: Boolean = false): ViewGroup {
    return LayoutInflater.from(viewGroup.context).inflate(layoutId, viewGroup, attachToRoot) as ViewGroup
}

inline fun inflateCustomView(context: Context, layoutId: Int, viewGroup: ViewGroup): View {
    return LayoutInflater.from(viewGroup.context).inflate(layoutId, viewGroup, true)
}
