package com.egoriku.catsrunning.kt_util

import android.animation.Animator

abstract class SimpleAnimatorListener : Animator.AnimatorListener {

    override fun onAnimationRepeat(p0: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
        super.onAnimationEnd(animation, isReverse)
    }

    override fun onAnimationEnd(p0: Animator?) {
    }

    override fun onAnimationCancel(p0: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
        super.onAnimationStart(animation, isReverse)
    }

    override fun onAnimationStart(p0: Animator?) {
    }
}