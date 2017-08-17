package com.egoriku.core_lib.adapter

import android.support.v7.widget.RecyclerView

abstract class AbstractAdapter<Item> : RecyclerView.Adapter<AbstractViewHolder>() {

    abstract fun onBind(holder: AbstractViewHolder, item: Item, position: Int, viewType: Int)

    abstract fun getItem(position: Int): Item

    override fun onBindViewHolder(holder: AbstractViewHolder, position: Int) {
        onBind(holder, getItem(position), position, getItemViewType(position))
    }

}
