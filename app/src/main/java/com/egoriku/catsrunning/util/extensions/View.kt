@file:Suppress("NOTHING_TO_INLINE")

package com.egoriku.catsrunning.util.extensions

import android.view.View
import android.view.View.*

inline fun View.hide(gone: Boolean = true) {
    visibility = if (gone) GONE else INVISIBLE
}

inline fun View.show() {
    visibility = VISIBLE
}
