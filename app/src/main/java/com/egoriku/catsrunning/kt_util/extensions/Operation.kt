package com.egoriku.catsrunning.kt_util.extensions

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}