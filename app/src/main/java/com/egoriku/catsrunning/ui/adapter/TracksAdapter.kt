package com.egoriku.catsrunning.ui.adapter

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.data.commons.TracksModel
import com.egoriku.catsrunning.helpers.Events
import com.egoriku.catsrunning.kt_util.Quadruple
import com.egoriku.catsrunning.kt_util.drawableTypeFit
import com.egoriku.catsrunning.kt_util.inflate
import com.egoriku.catsrunning.utils.TimeUtil
import com.egoriku.core_lib.adapter.AbstractAdapter
import com.egoriku.core_lib.adapter.AbstractViewHolder
import io.reactivex.subjects.PublishSubject

class TracksAdapter : AbstractAdapter<TracksModel>() {

    private var items: MutableList<TracksModel> = mutableListOf()
    val clickItem: PublishSubject<Pair<TracksModel, String>> = PublishSubject.create<Pair<TracksModel, @Events String>>()
    val likedClick: PublishSubject<Quadruple<LottieAnimationView, String, TracksModel, Int>> = PublishSubject.create<Quadruple<LottieAnimationView, @Events String, TracksModel, Int>>()

    companion object {
        private const val START_ANIMATION = 0.0f
        private const val END_ANIMATION = 1.0f
    }

    override fun onBind(holder: AbstractViewHolder, item: TracksModel, position: Int, viewType: Int) {
        val animationView = holder.get<LottieAnimationView>(R.id.liked_item)

        if (item.isFavorite) {
            animationView.progress = END_ANIMATION
        } else {
            animationView.progress = START_ANIMATION
        }

        holder.apply {
            get<TextView>(R.id.distance).text = String.format(getString(R.string.distance_format), item.distance)
            get<TextView>(R.id.calories).text = String.format(getString(R.string.calories_format), item.calories)
            get<TextView>(R.id.time_fit).text = TimeUtil.getTime(item.time)
            get<TextView>(R.id.date_fit).text = TimeUtil.convertUnixDateWithoutHours(item.beginsAt)
            get<ImageView>(R.id.ic_type_fit).setImageDrawable(drawableTypeFit(context, item.typeFit))

            itemView.setOnClickListener { clickItem.onNext(Pair(item, Events.CLICK)) }

            itemView.setOnLongClickListener {
                clickItem.onNext(Pair(item, Events.LONG_CLICK))
                true
            }

            animationView.setOnClickListener {
                likedClick.onNext(Quadruple(animationView, Events.LIKED_CLICK, item, position))
            }
        }
    }

    override fun getItem(position: Int): TracksModel {
        return items[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return AbstractViewHolder(parent.inflate(R.layout.item_tracks_adapter))
    }

    override fun getItemCount() = items.size

    fun setItems(item: List<TracksModel>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }
}