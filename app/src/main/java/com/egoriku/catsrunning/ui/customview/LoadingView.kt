package com.egoriku.catsrunning.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.egoriku.catsrunning.R
import com.egoriku.core_lib.extensions.drawableCompat
import com.egoriku.core_lib.extensions.hide
import com.egoriku.core_lib.extensions.inflateCustomView
import com.egoriku.core_lib.extensions.show
import kotlinx.android.synthetic.main.loading_view.view.*

class LoadingView : RelativeLayout {

    private lateinit var progressBar: ProgressBar
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
        initView(inflateCustomView(context, R.layout.loading_view, this))
    }

    private fun initView(view: View) {
        progressBar = view.progress_bar
        errorImage = view.error_image
        errorText = view.error_text
    }

    fun errorText(@StringRes resource: Int) = errorText(context.getString(resource))

    fun errorDrawable(@DrawableRes resource: Int) = errorDrawable(drawableCompat(context, resource))

    private fun errorText(errorText: String) {
        this.errorText.text = errorText
    }

    private fun errorDrawable(errorDrawable: Drawable?) {
        errorImage.setImageDrawable(errorDrawable)
    }

    fun showProgress(){
        progressBar.show()
    }

    fun hideProgress(){
        progressBar.hide()
    }

    fun hideErrorView(){
        errorImage.hide()
        errorText.hide()
    }

    fun showErrorView(){
        errorText.show()
        errorImage.show()
    }

    fun handleSuccessLoading(){
        hideProgress()
        hideErrorView()
    }
}