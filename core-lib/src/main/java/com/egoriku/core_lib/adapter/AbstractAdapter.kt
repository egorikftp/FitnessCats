package com.egoriku.core_lib.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.egoriku.core_lib.extensions.inflate

abstract class AbstractAdapter<Item> : RecyclerView.Adapter<AbstractViewHolder>() {

    abstract fun onCreateHolder(itemView: View, viewType: Int): AbstractViewHolder

    abstract fun onBindHolder(holder: AbstractViewHolder, item: Item, position: Int, viewType: Int)

    abstract fun getItem(position: Int): Item

    @LayoutRes
    abstract fun getLayout(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return onCreateHolder(parent.inflate(getLayout()), viewType)
    }

    override fun onBindViewHolder(holder: AbstractViewHolder, position: Int) {
        onBindHolder(holder, getItem(position), position, getItemViewType(position))
    }
}
