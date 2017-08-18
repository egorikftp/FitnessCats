package com.egoriku.core_lib.extensions

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}