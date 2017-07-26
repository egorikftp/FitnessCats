package com.egoriku.catsrunning.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.data.commons.TracksModel
import com.egoriku.catsrunning.helpers.Events
import com.egoriku.catsrunning.kt_util.drawableTypeFit
import com.egoriku.catsrunning.kt_util.Quadruple
import com.egoriku.catsrunning.kt_util.inflate
import com.egoriku.catsrunning.utils.ConverterTime
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_tracks_adapter.view.*

class TracksAdapter : RecyclerView.Adapter<TracksAdapter.TracksViewHolder>() {

    private var items: MutableList<TracksModel> = mutableListOf()
    val clickItem: PublishSubject<Pair<TracksModel, String>> = PublishSubject.create<Pair<TracksModel, @Events String>>()
    val likedClick: PublishSubject<Quadruple<LottieAnimationView, String, TracksModel, Int>> = PublishSubject.create<Quadruple<LottieAnimationView, @Events String, TracksModel, Int>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        return TracksViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TracksViewHolder?, position: Int) {
        holder?.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class TracksViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_tracks_adapter)) {

        fun bind(item: TracksModel) = with(itemView) {
            if (item.isFavorite) {
                liked_item.progress = 1.0f
            } else {
                liked_item.progress = 0.0f
            }

            distance.text = String.format(context.getString(R.string.distance_format), item.distance)
            ic_type_fit.setImageDrawable(drawableTypeFit(context, item.typeFit))
            date_fit.text = ConverterTime.convertUnixDateWithoutHours(item.beginsAt)
            calories.text = String.format(context.getString(R.string.calories_format), item.calories)
            time_fit.text = ConverterTime.getTime(item.time)

            itemView.setOnClickListener { clickItem.onNext(Pair(items[adapterPosition], Events.CLICK)) }

            itemView.setOnLongClickListener {
                clickItem.onNext(Pair(items[adapterPosition], Events.LONG_CLICK))
                true
            }

            liked_item.setOnClickListener {
                likedClick.onNext(Quadruple(liked_item, Events.LIKED_CLICK, items[adapterPosition], adapterPosition))
            }
        }
    }

    fun setItems(item: List<TracksModel>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }
}