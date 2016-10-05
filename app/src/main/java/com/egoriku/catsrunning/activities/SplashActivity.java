package com.egoriku.catsrunning.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.egoriku.catsrunning.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashActivity extends AppCompatActivity {
    private ImageView splashImage;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashImage = (ImageView) findViewById(R.id.splash_screen_cats_running);
        user = FirebaseAuth.getInstance().getCurrentUser();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_animation);
        splashImage.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (user != null) {
                    startActivity(new Intent(SplashActivity.this, TracksActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
