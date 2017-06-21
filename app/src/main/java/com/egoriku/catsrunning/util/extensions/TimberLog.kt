package com.egoriku.catsrunning.util.extensions

import timber.log.Timber

inline fun d(message: String, vararg args: Any) {
    Timber.d(message, args)
}

fun e(message: String, vararg args: Any) {
    Timber.e(message, args)
}

