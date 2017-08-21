package com.egoriku.core_lib.adapter.container

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.util.SparseArrayCompat
import com.egoriku.core_lib.extensions.drawableCompat

class DrawableContainer(val context: Context) : SparseArrayCompat<Drawable>() {

    fun getRes(@DrawableRes id: Int): Drawable {
        var drawable: Drawable? = get(id)

        if (drawable == null) {
            drawable = drawableCompat(context, id)
            put(id, drawable)
        }

        return drawable as Drawable
    }
}