package com.egoriku.core_lib.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.egoriku.core_lib.adapter.container.StringContainer
import com.egoriku.core_lib.adapter.container.ViewContainer

open class AbstractViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var viewContainer = ViewContainer(itemView)
    private var stringContainer = StringContainer(itemView)

    @Suppress("UNCHECKED_CAST")
    operator fun <T : View> get(viewId: Int): T {
        return viewContainer.getView(viewId) as T
    }

    fun getString(stringRes: Int): String {
        return stringContainer.getRes(stringRes)
    }

    val context: Context
        get() = itemView.context
}
