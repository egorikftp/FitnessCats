package com.egoriku.catsrunning.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.data.commons.TracksModel
import com.egoriku.catsrunning.kt_util.drawableCompat
import com.egoriku.catsrunning.kt_util.extensions.action
import com.egoriku.catsrunning.kt_util.extensions.snack
import com.egoriku.catsrunning.models.Constants.Extras.EXTRA_TRACK_ON_MAPS
import com.egoriku.catsrunning.utils.ConverterTime
import com.egoriku.catsrunning.utils.FirebaseUtils
import com.egoriku.catsrunning.utils.TypeFitBuilder.getTypeFit
import com.egoriku.catsrunning.utils.VectorToDrawable.createBitmapFromVector
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_track_map.*
import org.jetbrains.anko.toast

class TrackMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val firebaseUtils by lazy { FirebaseUtils.getInstance() }
    private var coordinatesList: MutableList<LatLng> = mutableListOf()

    private lateinit var startRunningHint: String
    private lateinit var endRunningHint: String

    private lateinit var tracksModel: TracksModel

    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_map)

        startRunningHint = getString(R.string.track_fragment_start_running_hint)
        endRunningHint = getString(R.string.track_fragment_end_running_hint)

        setSupportActionBar(toolbar_app)
        supportActionBar?.apply {
            title = getString(R.string.fragment_track_toolbar_title)
            setDisplayHomeAsUpEnabled(true)
        }

        if (savedInstanceState == null) {
            intent.extras.let {
                tracksModel = intent.extras.get(EXTRA_TRACK_ON_MAPS) as TracksModel
            }
        } else {
            tracksModel = savedInstanceState.getParcelable(EXTRA_TRACK_ON_MAPS)
        }

        for (i in 0..tracksModel.points.size - 1) {
            coordinatesList.add(LatLng(tracksModel.points[i].lat, tracksModel.points[i].lng))
        }

        track_on_map_distance.text = String.format(getString(R.string.track_fragment_distance_meter), tracksModel.distance)
        track_on_map_type_fit.text = String.format(getString(R.string.track_fragment_time_running), getTypeFit(tracksModel.typeFit, true, R.array.all_fitness_data_categories))
        track_on_map_time.text = ConverterTime.getTime(tracksModel.time)

        (supportFragmentManager.findFragmentById(R.id.track_on_maps_activity_map_fragment) as SupportMapFragment).getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(applicationContext, R.raw.maps_style))

        if (coordinatesList.isEmpty()) {
            toast(R.string.track_fragment_toast_text_no_points)
        } else {
            googleMap.addPolyline(PolylineOptions()
                    .addAll(coordinatesList)
                    .color(ContextCompat.getColor(this, R.color.colorAccent))
                    .width(10f)
            )

            val builder = LatLngBounds.Builder().apply {
                include(createMarker(googleMap, coordinatesList.first(), startRunningHint, R.drawable.ic_vec_location_start))
                include(createMarker(googleMap, coordinatesList.last(), endRunningHint, R.drawable.ic_vec_location_end))

                for (i in 1..coordinatesList.size - 2) {
                    include(coordinatesList[i])
                }
            }.build()

            googleMap.setOnMapLoadedCallback { googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder, paddingMap)) }
            googleMap.uiSettings.isZoomControlsEnabled = true
        }
    }

    private fun createMarker(map: GoogleMap, latLng: LatLng, title: String, @DrawableRes idIco: Int): LatLng {
        return map.addMarker(MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(createBitmapFromVector(resources, idIco))))
                .position
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.track_on_map_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_tracks_on_map_activity_action_like).icon = when (tracksModel.isFavorite) {
            true -> drawableCompat(this, R.drawable.ic_vec_star_white)
            false -> drawableCompat(this, R.drawable.ic_vec_star_border_white)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_tracks_on_map_activity_action_like -> {
                tracksModel.isFavorite = !tracksModel.isFavorite
                firebaseUtils.updateFavorite(tracksModel, this)
                invalidateOptionsMenu()
                return true
            }

            R.id.menu_tracks_on_map_activity_action_delete -> {
                activity_track_map_root.snack(R.string.track_on_map_activity_track_delete) {
                    action(R.string.track_on_map_activity_cancel_delete) {
                        activity_track_map_root.snack(R.string.track_on_map_activity_track_cancel_delete_success, Snackbar.LENGTH_SHORT)
                    }
                    addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(snackbar: Snackbar?, event: Int) {
                            when (event) {
                                Snackbar.Callback.DISMISS_EVENT_TIMEOUT -> firebaseUtils.removeTrack(tracksModel, this@TrackMapActivity)
                            }
                        }
                    })
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(EXTRA_TRACK_ON_MAPS, tracksModel)
    }

    companion object {
        private val paddingMap = 150

        @Deprecated("Will remove after converting to kotlin")
        fun start(context: Context, tracksModel: TracksModel) {
            context.startActivity(Intent(context, TrackMapActivity::class.java)
                    .putExtra(EXTRA_TRACK_ON_MAPS, tracksModel))
        }
    }
}
