package com.egoriku.catsrunning.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.egoriku.catsrunning.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private ImageView splashImage;
    private static final String PROPERTY_NAME = "rotation";
    private long delayStartActivity = 3000L;
    private static final float startRotation = 0f;
    private static final float stopRotation = 720f;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashImage = (ImageView) findViewById(R.id.splash_screen_cats_running);
        user = FirebaseAuth.getInstance().getCurrentUser();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(splashImage, PROPERTY_NAME, startRotation, stopRotation);
        rotationAnim.setDuration(5000);
        rotationAnim.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
                    finish();
                }
            }
        }, delayStartActivity);
    }
}
