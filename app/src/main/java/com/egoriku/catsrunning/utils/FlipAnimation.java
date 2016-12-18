package com.egoriku.catsrunning.utils;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

public class FlipAnimation extends Animation {
    private Camera camera;
    private View btnStart;
    private View btnFinish;
    private View viewNowTime;
    private View viewNowDistance;
    private View imageFinishScamper;
    private View textViewFinalTime;
    private View textViewFinalDistance;
    private float centerX;
    private float centerY;
    private boolean isReverse = false;


    public FlipAnimation(View btnStart, View btnFinish, View viewNowTime, View textNowDistance, View imageFinishScamper, TextView textViewFinalTime, TextView textViewFinalDistance) {
        this.btnStart = btnStart;
        this.btnFinish = btnFinish;
        this.viewNowTime = viewNowTime;
        this.viewNowDistance = textNowDistance;
        this.imageFinishScamper = imageFinishScamper;
        this.textViewFinalTime = textViewFinalTime;
        this.textViewFinalDistance = textViewFinalDistance;

        setDuration(700);
        setFillAfter(false);
        setInterpolator(new AccelerateDecelerateInterpolator());
    }


    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        centerX = width / 2;
        centerY = height / 2;
        camera = new Camera();
    }


    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float degrees = (float) (180.0 * Math.PI * interpolatedTime / Math.PI);

        if (interpolatedTime >= 0.5f) {
            degrees -= 180.f;
            btnStart.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
            viewNowTime.setVisibility(View.VISIBLE);
            viewNowDistance.setVisibility(View.VISIBLE);
        }

        if (isReverse) {
            degrees = -degrees;
            btnFinish.setVisibility(View.GONE);
            viewNowDistance.setVisibility(View.GONE);
            viewNowTime.setVisibility(View.GONE);
            imageFinishScamper.setVisibility(View.VISIBLE);
            textViewFinalTime.setVisibility(View.VISIBLE);
            textViewFinalDistance.setVisibility(View.VISIBLE);
        }

        final Matrix matrix = t.getMatrix();
        camera.save();
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }


    public void setReverse() {
        isReverse = true;
    }
}
