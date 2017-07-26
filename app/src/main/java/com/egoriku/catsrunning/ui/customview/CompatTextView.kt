package com.egoriku.catsrunning.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.util.AttributeSet
import android.widget.TextView
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.kt_util.drawableCompat
import com.egoriku.catsrunning.kt_util.extensions.fromApi
import com.egoriku.catsrunning.kt_util.extensions.toApi


class CompatTextView : TextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val attributeArray = context.obtainStyledAttributes(attrs, R.styleable.CompatTextView)

            var drawableLeft: Drawable? = null
            var drawableRight: Drawable? = null
            var drawableBottom: Drawable? = null
            var drawableTop: Drawable? = null

            fromApi(LOLLIPOP) {
                drawableLeft = attributeArray.getDrawable(R.styleable.CompatTextView_drawableLeftCompat)
                drawableRight = attributeArray.getDrawable(R.styleable.CompatTextView_drawableRightCompat)
                drawableBottom = attributeArray.getDrawable(R.styleable.CompatTextView_drawableBottomCompat)
                drawableTop = attributeArray.getDrawable(R.styleable.CompatTextView_drawableTopCompat)
            }

            toApi(LOLLIPOP) {
                val drawableLeftId = attributeArray.getResourceId(R.styleable.CompatTextView_drawableLeftCompat, -1)
                val drawableRightId = attributeArray.getResourceId(R.styleable.CompatTextView_drawableRightCompat, -1)
                val drawableBottomId = attributeArray.getResourceId(R.styleable.CompatTextView_drawableBottomCompat, -1)
                val drawableTopId = attributeArray.getResourceId(R.styleable.CompatTextView_drawableTopCompat, -1)

                if (drawableLeftId != -1) drawableLeft = drawableCompat(context, drawableLeftId)
                if (drawableRightId != -1) drawableRight = drawableCompat(context, drawableRightId)
                if (drawableBottomId != -1) drawableBottom = drawableCompat(context, drawableBottomId)
                if (drawableTopId != -1) drawableTop = drawableCompat(context, drawableTopId)
            }

            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom)
            attributeArray.recycle()
        }
    }
}