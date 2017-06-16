package com.egoriku.catsrunning.activities

import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.egoriku.catsrunning.BuildConfig
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.activities.SplashActivity.Constant.IS_ANIMATE
import com.egoriku.catsrunning.util.SimpleAnimationListener
import com.egoriku.catsrunning.util.snack
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.ResultCodes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import java.util.*


class SplashActivity : AppCompatActivity() {

    object Constant {
        const val IS_ANIMATE = "is_animate"
    }

    private val RC_SIGN_IN = 4706
    private lateinit var animation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (intent.extras == null || !intent.extras.getBoolean(IS_ANIMATE)) {
            animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_empty_animation)
        } else {
            animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_rotate_animation)
        }

        splash_app_logo.startAnimation(animation)

        animation.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationEnd(animation: Animation?) {
                if (FirebaseAuth.getInstance().currentUser != null) {
                    startActivity<TracksActivity>()
                    finish()
                    animateTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(
                                            Arrays.asList<AuthUI.IdpConfig>(
                                                    AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                    AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .setTheme(R.style.AuthTheme)
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                    .setLogo(R.drawable.ic_cats_no_track)
                                    .build(),
                            RC_SIGN_IN)
                    animateTransition(R.anim.slide_in_left, R.anim.slide_out_righ)
                }
            }
        })
    }

    private fun animateTransition(enterAnim: Int, exitAnim: Int) {
        overridePendingTransition(enterAnim, exitAnim)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == ResultCodes.OK) {
                startActivity<TracksActivity>()
                finish()
                return
            } else {
                if (response == null) {
                    showMessage(R.string.auth_cancel)
                    return
                }

                when (response.errorCode) {
                    ErrorCodes.NO_NETWORK -> {
                        showMessage(R.string.auth_no_internet_connection)
                        return
                    }
                    ErrorCodes.UNKNOWN_ERROR -> {
                        showMessage(R.string.auth_unknown_error)
                        return
                    }
                }
            }
            splash_screen_container.snack(R.string.auth_unknown_sign_in_response)
        }
    }

    private fun showMessage(@StringRes messageId: Int) {
        splash_screen_container.snack(messageId)
    }

}
