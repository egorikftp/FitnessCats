package com.egoriku.catsrunning.helpers

import android.support.annotation.IntDef

class TypeFit {

    companion object {
        const val WALKING = 1L
        const val REMINDER = 2L
        const val MAIN = 3L
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(WALKING, REMINDER, MAIN)
    annotation class TypeFit
}

