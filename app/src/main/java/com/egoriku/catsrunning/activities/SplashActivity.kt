package com.egoriku.catsrunning.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.egoriku.catsrunning.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import util.SimpleAnimationListener

class SplashActivity : AppCompatActivity() {

    private lateinit var animation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_animation)

        splash_screen_cats_running.startAnimation(animation)

        animation.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationEnd(animation: Animation?) {
                if (FirebaseAuth.getInstance().currentUser != null) {
                    startActivity<TracksActivity>()
                    animateTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                    startActivity<RegisterActivity>()
                    animateTransition(R.anim.slide_in_left, R.anim.slide_out_righ)
                }
            }
        })
    }

    private fun animateTransition(enterAnim: Int, exitAnim: Int) {
        overridePendingTransition(enterAnim, exitAnim)
        finish()
    }
}
