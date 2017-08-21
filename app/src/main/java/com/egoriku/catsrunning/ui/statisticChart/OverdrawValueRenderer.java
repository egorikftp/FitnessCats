package com.egoriku.catsrunning.ui.statisticChart;

import android.graphics.Path;
import android.graphics.RectF;


public class OverdrawValueRenderer {
    private RectF drawingArea;
    private FitChartValue value;
    private int index;
    private float strokeSize;


    public OverdrawValueRenderer(RectF drawingArea, FitChartValue value, int index, float strokeSize) {
        this.drawingArea = drawingArea;
        this.value = value;
        this.index = index;
        this.strokeSize = strokeSize;
    }


    public Path buildPath(float animationProgress) {
        float startAngle = FitChart.START_ANGLE;
        float valueSweepAngle = value.getSweepAngle();
        valueSweepAngle -= startAngle;
        float sweepAngle = valueSweepAngle * animationProgress;

        Path linePath = new Path();
        RectF defaultRectF = drawingArea;
        RectF rectF = new RectF(defaultRectF);

        if (index == 1) {
            rectF.set(
                    defaultRectF.left + strokeSize,
                    defaultRectF.top + strokeSize,
                    defaultRectF.right - strokeSize,
                    defaultRectF.bottom - strokeSize
            );
        }

        if (index == 2) {
            rectF.set(
                    defaultRectF.left + strokeSize*index,
                    defaultRectF.top + strokeSize*index,
                    defaultRectF.right - strokeSize*index,
                    defaultRectF.bottom - strokeSize*index
            );
        }

        linePath.addArc(rectF, startAngle, sweepAngle);
        return linePath;
    }
}
