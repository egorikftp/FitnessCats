package com.egoriku.catsrunning.ui.statisticChart;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.egoriku.catsrunning.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.egoriku.catsrunning.models.Constants.FitChart.ANIMATION_SEEK;

public class FitChart extends View {
    static final int DEFAULT_VIEW_RADIUS = 0;
    static final int DEFAULT_MIN_VALUE = 0;
    static final int DEFAULT_MAX_VALUE = 100;
    static final int START_ANGLE = -90;
    static final int ANIMATION_DURATION = 1500;
    static final float INITIAL_ANIMATION_PROGRESS = 0.0f;
    static final int DESIGN_MODE_SWEEP_ANGLE = 216;
    private RectF drawingArea;
    private Paint valueDesignPaint;
    private int valueStrokeColor;

    private float strokeSize;
    private float minValue = DEFAULT_MIN_VALUE;
    private float maxValue = DEFAULT_MAX_VALUE;
    private List<FitChartValue> chartValues;
    private float animationProgress = INITIAL_ANIMATION_PROGRESS;

    public FitChart(Context context) {
        super(context);
        initializeView(null);
    }


    public FitChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView(attrs);
    }


    public FitChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView(attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDrawableArea();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = Math.max(width, height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        renderValues(canvas);
    }


    public void setMinValue(float value) {
        minValue = value;
    }


    public void setMaxValue(float value) {
        maxValue = value;
    }


    //single value
    public void setValue(float value) {
        chartValues.clear();
        FitChartValue chartValue = new FitChartValue(value, valueStrokeColor);
        chartValue.setPaint(buildPaintForValue());
        chartValue.setStartAngle(START_ANGLE);
        chartValue.setSweepAngle(calculateSweepAngle(value));
        chartValues.add(chartValue);
        playAnimation();
    }


    //collection value
    public void setValues(Collection<FitChartValue> values) {
        chartValues.clear();
        for (FitChartValue chartValue : values) {
            float sweepAngle = calculateSweepAngle(chartValue.getValue());
            chartValue.setPaint(buildPaintForValue());
            chartValue.setStartAngle(START_ANGLE);
            chartValue.setSweepAngle(sweepAngle);
            chartValues.add(chartValue);
        }
        playAnimation();
    }


    private void initializeView(AttributeSet attrs) {
        chartValues = new ArrayList<>();
        readAttributes(attrs);
    }


    private void calculateDrawableArea() {
        float drawPadding = (strokeSize / 2);
        float width = getWidth();
        float height = getHeight();
        float left = drawPadding;
        float top = drawPadding;
        float right = width - drawPadding;
        float bottom = height - drawPadding;
        drawingArea = new RectF(left, top, right, bottom);
    }


    private void readAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FitChart, 0, 0);
            strokeSize = attributes.getDimensionPixelSize(R.styleable.FitChart_strokeSize, (int) strokeSize);
            valueStrokeColor = attributes.getColor(R.styleable.FitChart_valueStrokeColor, valueStrokeColor);
            attributes.recycle();
        }
    }


    private Paint getPaint() {
        if (!isInEditMode()) {
            return new Paint(Paint.ANTI_ALIAS_FLAG);
        } else {
            return new Paint();
        }
    }


    private float getViewRadius() {
        if (drawingArea != null) {
            return (drawingArea.width() / 2);
        } else {
            return DEFAULT_VIEW_RADIUS;
        }
    }


    private void renderValues(Canvas canvas) {
        if (!isInEditMode()) {
            int valuesCounter = (chartValues.size() - 1);
            for (int index = valuesCounter; index >= 0; index--) {
                renderValue(canvas, chartValues.get(index), index);
            }
        } else {
            renderValue(canvas, null, -1);
        }
    }


    private void renderValue(Canvas canvas, FitChartValue value, int index) {
        if (!isInEditMode()) {
            Path path = new OverdrawValueRenderer(drawingArea, value, index, strokeSize).buildPath(animationProgress);

            if (path != null) {
                canvas.drawPath(path, value.getPaint());
            }
        } else {
            Path path = new Path();
            path.addArc(drawingArea, START_ANGLE, DESIGN_MODE_SWEEP_ANGLE);
            canvas.drawPath(path, valueDesignPaint);
        }
    }


    private float calculateSweepAngle(float value) {
        float chartValuesWindow = Math.max(minValue, maxValue) - Math.min(minValue, maxValue);
        float chartValuesScale = (360f / chartValuesWindow);
        return (value * chartValuesScale);
    }


    private void playAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, ANIMATION_SEEK, 0.0f, 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setTarget(this);
        animatorSet.play(animator);
        animatorSet.start();
    }


    private void setAnimationSeek(float value) {
        animationProgress = value;
        invalidate();
    }


    private Paint buildPaintForValue() {
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeSize);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }
}
