package com.egoriku.catsrunning.kt_util.extensions

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View

fun View.snack(@StringRes messageId: Int, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, messageId, length)
    snack.f()
    snack.show()
}

fun View.snack(@StringRes messageId: Int, length: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, messageId, length).show()
}

fun Snackbar.action(@StringRes textResId: Int, @ColorInt color: Int? = null, listener: (View) -> Unit) {
    setAction(textResId, listener)
    color?.let { setActionTextColor(color) }
}