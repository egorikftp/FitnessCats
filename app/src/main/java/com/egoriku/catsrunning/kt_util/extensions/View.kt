@file:Suppress("NOTHING_TO_INLINE")

package com.egoriku.catsrunning.kt_util.extensions

import android.view.View

inline fun View.hide(gone: Boolean = true) {
    visibility = if (gone) View.GONE else View.INVISIBLE
}

inline fun View.show() {
    visibility = View.VISIBLE
}
