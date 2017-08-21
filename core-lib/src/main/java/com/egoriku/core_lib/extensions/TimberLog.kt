@file:Suppress("NOTHING_TO_INLINE")

package com.egoriku.core_lib.extensions

import timber.log.Timber

inline fun d(message: String, vararg args: Any) {
    Timber.d(message, args)
}

inline fun e(message: String, vararg args: Any) {
    Timber.e(message, args)
}

