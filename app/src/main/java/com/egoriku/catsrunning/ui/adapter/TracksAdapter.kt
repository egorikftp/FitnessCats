package com.egoriku.catsrunning.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.data.commons.TracksModel
import com.egoriku.catsrunning.util.drawableCompat
import com.egoriku.catsrunning.util.drawableTypeFit
import com.egoriku.catsrunning.util.inflate
import com.egoriku.catsrunning.utils.ConverterTime
import kotlinx.android.synthetic.main.adapter_fitness_data_fragment.view.*

class TracksAdapter(val viewActions: onViewSelectedListener) : RecyclerView.Adapter<TracksAdapter.TracksViewHolder>() {

    private var items: MutableList<TracksModel> = mutableListOf()

    interface onViewSelectedListener {
        fun onClickItem(item: TracksModel)
        fun onFavoriteClick(item: TracksModel)
        fun onLongClick(item: TracksModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        return TracksViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TracksViewHolder?, position: Int) {
        holder?.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class TracksViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.adapter_fitness_data_fragment)) {

        fun bind(item: TracksModel) = with(itemView) {
            if (item.isFavorite) {
                liked_item.setImageDrawable(drawableCompat(context, R.drawable.ic_vec_star_black))
            } else {
                liked_item.setImageDrawable(drawableCompat(context, R.drawable.ic_vec_star_border))
            }

            distance.text = String.format(context.getString(R.string.distance_format), item.distance)
            ic_type_fit.setImageDrawable(drawableTypeFit(context, item.typeFit))
            date_fit.text = ConverterTime.convertUnixDateWithoutHours(item.beginsAt)
            calories.text = String.format(context.getString(R.string.calories_format), item.calories)
            time_fit.text = ConverterTime.ConvertTimeAllFitnessData(item.beginsAt, item.time)

            itemView.setOnClickListener { viewActions.onClickItem(items[adapterPosition]) }
            itemView.setOnLongClickListener {
                viewActions.onLongClick(item)
                true
            }

            liked_item.setOnClickListener {
                viewActions.onFavoriteClick(item)
            }
        }
    }

    fun setItems(item: List<TracksModel>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }
}