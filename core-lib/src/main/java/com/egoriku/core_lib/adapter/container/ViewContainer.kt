package com.egoriku.core_lib.adapter.container

import android.support.annotation.IdRes
import android.support.v4.util.SparseArrayCompat
import android.view.View

class ViewContainer(private var itemView: View) : SparseArrayCompat<View>() {

    @Suppress("UNCHECKED_CAST")
    fun getView(@IdRes id: Int): View {
        var view: View? = get(id)

        if (view == null) {
            view = itemView.findViewById(id)
            put(id, view)
        }

        return view as View
    }
}