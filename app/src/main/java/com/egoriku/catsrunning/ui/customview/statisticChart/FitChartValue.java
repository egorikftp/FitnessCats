package com.egoriku.catsrunning.ui.customview.statisticChart;

import android.graphics.Paint;

public class FitChartValue {
    private final int value;
    private final int color;
    private Paint paint;
    private float startAngle;
    private float sweepAngle;

    public FitChartValue(int value, int color) {
        this.value = value;
        this.color = color;
    }

    public float getValue() {
        return value;
    }

    public int getColor() {
        return color;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
        this.paint.setColor(color);
    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }
}
