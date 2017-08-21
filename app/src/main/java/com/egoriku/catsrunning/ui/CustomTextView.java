package com.egoriku.catsrunning.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.egoriku.catsrunning.utils.CustomFont;

public class CustomTextView extends TextView {
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(CustomFont.getTypeFace());
    }
}
