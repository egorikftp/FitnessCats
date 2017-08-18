package com.egoriku.core_lib.extensions

fun runAsync(action: () -> Unit) = Thread(Runnable(action)).start()

fun runOnUiThread(action: () -> Unit) {
    if (isMainLooperAlive()) {
        action()
    } else {
        android.os.Handler(android.os.Looper.getMainLooper()).post(Runnable(action))
    }
}

fun runDelayed(delayMillis: Long, action: () -> Unit) = android.os.Handler().postDelayed(Runnable(action), delayMillis)

fun runDelayedOnUiThread(delayMillis: Long, action: () -> Unit) = android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(Runnable(action), delayMillis)

private fun isMainLooperAlive() = android.os.Looper.myLooper() == android.os.Looper.getMainLooper()