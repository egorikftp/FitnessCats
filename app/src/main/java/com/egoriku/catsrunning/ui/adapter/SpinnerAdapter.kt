package com.egoriku.catsrunning.ui.adapter

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.data.commons.SpinnerIntervalModel
import com.egoriku.catsrunning.kt_util.inflateViewGroup
import kotlinx.android.synthetic.main.item_spinner.view.*

class SpinnerAdapter(context: Context, private val items: List<SpinnerIntervalModel>)
    : ArrayAdapter<SpinnerIntervalModel>(context, R.layout.item_spinner, items) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewGroup: ViewGroup
        val viewHolder: ViewHolder

        if (convertView != null) {
            viewGroup = convertView as ViewGroup
            viewHolder = convertView.tag as ViewHolder
        } else {
            viewGroup = inflateViewGroup(parent, R.layout.item_spinner)
            viewHolder = ViewHolder(viewGroup)
            viewGroup.tag = viewHolder
        }

        viewHolder.someInterval.text = items[position].nameInterval
        viewHolder.someInterval.typeface = ResourcesCompat.getFont(parent.context, R.font.birusa)
        return viewGroup
    }

    inner class ViewHolder(viewGroup: ViewGroup) {
        val someInterval: TextView = viewGroup.item_interval
    }
}
