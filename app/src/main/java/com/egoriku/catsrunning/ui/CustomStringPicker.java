package com.egoriku.catsrunning.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static com.egoriku.catsrunning.models.Constants.CustomStringPicker.ANDROID_WIDGET_NUMBER_PICKER;
import static com.egoriku.catsrunning.models.Constants.CustomStringPicker.COM_ANDROID_INTERNAL_WIDGET_NUMBER_PICKER;
import static com.egoriku.catsrunning.models.Constants.CustomStringPicker.GET_CURRENT;
import static com.egoriku.catsrunning.models.Constants.CustomStringPicker.GET_VALUE;
import static com.egoriku.catsrunning.models.Constants.CustomStringPicker.SET_CURRENT;
import static com.egoriku.catsrunning.models.Constants.CustomStringPicker.SET_DESCENDANT_FOCUSABILITY;
import static com.egoriku.catsrunning.models.Constants.CustomStringPicker.SET_DISPLAYED_VALUES;
import static com.egoriku.catsrunning.models.Constants.CustomStringPicker.SET_MAX_VALUE;
import static com.egoriku.catsrunning.models.Constants.CustomStringPicker.SET_MIN_VALUE;
import static com.egoriku.catsrunning.models.Constants.CustomStringPicker.SET_RANGE;
import static com.egoriku.catsrunning.models.Constants.CustomStringPicker.SET_VALUE;

public class CustomStringPicker extends LinearLayout {
    private Object mInstance;
    private Class<?> mClazz;
    private String[] values;
    private static final int SDK_VERSION;
    private static final String PICKER_CLASS;

    static {
        SDK_VERSION = Build.VERSION.SDK_INT;
        PICKER_CLASS = SDK_VERSION < Build.VERSION_CODES.FROYO ? COM_ANDROID_INTERNAL_WIDGET_NUMBER_PICKER : ANDROID_WIDGET_NUMBER_PICKER;
    }


    public CustomStringPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public void setCurrent(final int current) {
        String methodName = isUnderHoneyComb() ? SET_CURRENT : SET_VALUE;
        try {
            Method method = mClazz.getMethod(methodName, int.class);
            method.invoke(mInstance, current);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }


    public int getCurrent() {
        String methodName = isUnderHoneyComb() ? GET_CURRENT : GET_VALUE;
        try {
            Method method = mClazz.getMethod(methodName);
            return (Integer) method.invoke(mInstance);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getCurrentValue() {
        return values[getCurrent()];
    }


    public void setValues(final String[] values) {
        this.values = values;
        if (isUnderHoneyComb()) {
            try {
                Method method = mClazz.getMethod(SET_RANGE, int.class, int.class, String[].class);
                method.invoke(mInstance, 0, values.length - 1, values);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                mClazz.getMethod(SET_MAX_VALUE, int.class).invoke(mInstance, values.length - 1);
                mClazz.getMethod(SET_MIN_VALUE, int.class).invoke(mInstance, 0);
                mClazz.getMethod(SET_DISPLAYED_VALUES, String[].class).invoke(mInstance, new Object[]{values});
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void setValues(final List<String> values) {
        this.values = values.toArray(new String[values.size()]);
        setValues(this.values);
    }


    private void init(final Context context, final AttributeSet attrs) {
        try {
            Class<?> clazz = context.getClassLoader().loadClass(PICKER_CLASS);
            Constructor<?> constructor = clazz.getConstructor(Context.class, AttributeSet.class);
            mInstance = constructor.newInstance(context, attrs);
            mClazz = mInstance.getClass();

            String methodName = SET_DESCENDANT_FOCUSABILITY;
            Method method = mClazz.getMethod(methodName, int.class);
            method.invoke(mInstance, NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        addView((View) mInstance);
        setOrientation(VERTICAL);
    }


    private static boolean isUnderHoneyComb() {
        return SDK_VERSION < Build.VERSION_CODES.HONEYCOMB;
    }
}
