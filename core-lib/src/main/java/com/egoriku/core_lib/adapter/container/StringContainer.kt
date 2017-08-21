package com.egoriku.core_lib.adapter.container

import android.content.res.Resources
import android.support.annotation.StringRes
import android.support.v4.util.SparseArrayCompat
import android.view.View

class StringContainer(itemView: View) : SparseArrayCompat<String>() {

    private val res: Resources = itemView.resources

    fun getRes(@StringRes id: Int): String {
        var srt: String? = get(id)

        if (srt == null) {
            srt = res.getString(id)
            put(id, srt)
        }

        return srt as String
    }
}