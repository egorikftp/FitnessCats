package com.egoriku.catsrunning.util

import timber.log.Timber

fun d(message: String, vararg args: Any) {
    Timber.d(message, args)
}

fun e(message: String, vararg args: Any) {
    Timber.e(message, args)
}

