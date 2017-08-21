package com.egoriku.catsrunning.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.data.commons.TracksModel
import com.egoriku.catsrunning.extensions.typeFitIcon
import com.egoriku.catsrunning.helpers.Events
import com.egoriku.catsrunning.utils.TimeUtil
import com.egoriku.core_lib.adapter.AbstractAdapter
import com.egoriku.core_lib.adapter.AbstractViewHolder
import com.egoriku.core_lib.extensions.Quadruple
import io.reactivex.subjects.PublishSubject

class TracksAdapter : AbstractAdapter<TracksModel>() {

    private var items: MutableList<TracksModel> = mutableListOf()
    val clickItem: PublishSubject<Pair<TracksModel, String>> = PublishSubject.create<Pair<TracksModel, @Events String>>()
    val likedClick: PublishSubject<Quadruple<ImageView, String, TracksModel, Int>> = PublishSubject.create<Quadruple<ImageView, @Events String, TracksModel, Int>>()

    override fun onBindHolder(holder: AbstractViewHolder, item: TracksModel, position: Int, viewType: Int) {
        holder.apply {
            val liked = holder.get<ImageView>(R.id.liked_item)
            get<TextView>(R.id.distance).text = String.format(getString(R.string.distance_format), item.distance)
            get<TextView>(R.id.calories).text = String.format(getString(R.string.calories_format), item.calories)
            get<TextView>(R.id.time_fit).text = TimeUtil.getTime(item.time)
            get<TextView>(R.id.date_fit).text = TimeUtil.convertUnixDateWithoutHours(item.beginsAt)
            get<ImageView>(R.id.ic_type_fit).setImageDrawable(getDrawable(typeFitIcon(item.typeFit)))

            if (item.isFavorite) {
                liked.setImageDrawable(getDrawable(R.drawable.ic_vec_star_black))
            } else {
                liked.setImageDrawable(getDrawable(R.drawable.ic_vec_star_border))
            }

            itemView.setOnClickListener { clickItem.onNext(Pair(item, Events.CLICK)) }

            itemView.setOnLongClickListener {
                clickItem.onNext(Pair(item, Events.LONG_CLICK))
                true
            }

            liked.setOnClickListener {
                likedClick.onNext(Quadruple(liked, Events.LIKED_CLICK, item, position))
            }
        }
    }

    override fun getItem(position: Int): TracksModel {
        return items[position]
    }

    override fun onCreateHolder(itemView: View, viewType: Int): AbstractViewHolder {
        return AbstractViewHolder(itemView)
    }

    override fun getLayout(): Int {
        return R.layout.item_tracks_adapter
    }

    override fun getItemCount() = items.size

    fun setItems(item: List<TracksModel>) {
        // val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(DiffCallback(item, items))

        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
        // diffResult.dispatchUpdatesTo(this)
    }
}