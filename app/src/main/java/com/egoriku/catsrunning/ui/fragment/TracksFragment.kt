package com.egoriku.catsrunning.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.activities.FitActivity
import com.egoriku.catsrunning.activities.TrackOnMapsActivity
import com.egoriku.catsrunning.data.TracksDataManager
import com.egoriku.catsrunning.data.UIListener
import com.egoriku.catsrunning.data.commons.TracksModel
import com.egoriku.catsrunning.helpers.Events
import com.egoriku.catsrunning.helpers.TypeFit
import com.egoriku.catsrunning.models.Constants
import com.egoriku.catsrunning.ui.activity.TracksActivity
import com.egoriku.catsrunning.ui.adapter.TracksAdapter
import com.egoriku.catsrunning.util.drawableCompat
import com.egoriku.catsrunning.util.extensions.hide
import com.egoriku.catsrunning.util.extensions.show
import com.egoriku.catsrunning.util.inflate
import com.egoriku.catsrunning.utils.FirebaseUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_tracks.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity

class TracksFragment : Fragment(), UIListener {

    private lateinit var adapter: TracksAdapter
    private val tracksDataManager = TracksDataManager.instance
    private var anim: TranslateAnimation = TranslateAnimation(0f, 0f, 40f, 0f)
    private val firebaseUtils by lazy { FirebaseUtils.getInstance() }
    private lateinit var subscriber: Disposable

    init {
        anim.apply {
            duration = 200
            interpolator = LinearOutSlowInInterpolator()
            fillAfter = true
        }
    }

    companion object {
        fun instance(): TracksFragment {
            return TracksFragment()
        }
    }

    override fun handleSuccess(data: List<TracksModel>) {
        progressbar.hide()
        tracks_recyclerview.show()
        adapter.setItems(data)
        no_tracks.hide()
        no_tracks_text.hide()

        if (data.isEmpty()) {
            no_tracks.show()
            no_tracks.setImageDrawable(drawableCompat(activity, R.drawable.ic_vec_cats_no_track))
            no_tracks_text.show()
            tracks_recyclerview.hide(gone = false)
        }
    }

    override fun handleError() {
    }

    override fun onStart() {
        super.onStart()
        (activity as TracksActivity).onFragmentStart(R.string.navigation_drawer_main_activity_new)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_tracks)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tracks_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        progressbar.show()
        initAdapter()
        tracksDataManager.apply {
            addListener(this@TracksFragment)
            loadTracks(TypeFit.WALKING)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_walking -> consumeEvent(TypeFit.WALKING, it.itemId)
                R.id.action_running -> consumeEvent(TypeFit.RUNNING, it.itemId)
                R.id.action_cycling -> consumeEvent(TypeFit.CYCLING, it.itemId)
                else -> false
            }
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    inline private fun consumeEvent(typeFit: Int, itemId: Int): Boolean {
        if (bottomNavigationView.selectedItemId != itemId) {
            tracks_recyclerview.animation = anim
            anim.start()
        }
        tracksDataManager.loadTracks(typeFit)
        return true
    }

    private fun initAdapter() {
        if (tracks_recyclerview.adapter == null) {
            adapter = TracksAdapter()
            tracks_recyclerview.adapter = adapter

            subscriber = adapter.clickItem
                    .subscribe({ (tracksModel, event) ->
                        when (event) {
                            Events.CLICK -> {
                                if (tracksModel.time == 0L) {
                                    startActivity<FitActivity>(Constants.Extras.KEY_TYPE_FIT to tracksModel.typeFit)
                                } else {
                                    startActivity<TrackOnMapsActivity>(Constants.Extras.EXTRA_TRACK_ON_MAPS to tracksModel)
                                }
                            }
                            Events.LONG_CLICK -> {
                                alert(R.string.fitness_data_fragment_alert_title) {
                                    positiveButton(R.string.fitness_data_fragment_alert_positive_btn) { firebaseUtils.removeTrack(tracksModel, context) }
                                    negativeButton(R.string.fitness_data_fragment_alert_negative_btn) {}
                                }.show()
                            }
                            Events.LIKED_CLICK -> {
                                tracksModel.isFavorite = !tracksModel.isFavorite
                                firebaseUtils.updateTrackFavorire(tracksModel, context)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    })
        }
    }

    override fun onStop() {
        super.onStop()
        tracksDataManager.removeUIListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriber.dispose()
    }
}