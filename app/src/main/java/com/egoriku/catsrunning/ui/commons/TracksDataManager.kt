package com.egoriku.catsrunning.ui.commons

import com.egoriku.catsrunning.helpers.TypeFit
import com.egoriku.catsrunning.models.Constants
import com.egoriku.catsrunning.data.TracksModel
import com.egoriku.catsrunning.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class TracksDataManager private constructor() : ValueEventListener {

    override fun onCancelled(databaseError: DatabaseError?) {
        uiListener?.handleError()
    }

    override fun onDataChange(dataSnapshot: DataSnapshot?) {
        dataSnapshot?.children?.forEach {
            val value = it.toModelOfType<TracksModel>()
            if (value != null) {
                tracks.add(value)
            }
        }

        if (tracks.isEmpty()) uiListener?.handleError()
        else {
            notifySuccess(typeFit)
        }
    }

    private val tracks = ArrayList<TracksModel>()
    private var uiListener: UIListener? = null
    private var typeFit: Int = 0

    fun addListener(listener: UIListener) {
        uiListener = listener
    }

    fun removeUIListener() {
        uiListener = null
        FirebaseUtils.getDatabaseReference()
                .child(Constants.FirebaseFields.TRACKS)
                .child(FirebaseAuth.getInstance().currentUser?.uid)
                .removeEventListener(this)
    }

    fun loadTracks(typeFit: Int) {
        this.typeFit = typeFit
        if (tracks.isEmpty()) {
            FirebaseUtils.getDatabaseReference()
                    .child(Constants.FirebaseFields.TRACKS)
                    .child(FirebaseAuth.getInstance().currentUser?.uid)
                    .addListenerForSingleValueEvent(this)
        } else {
            notifySuccess(typeFit)
        }
    }

    inline private fun notifySuccess(typeFit: Int) {
        when (typeFit) {
            TypeFit.WALKING -> uiListener?.handleSuccess(walkingData())
            TypeFit.RUNNING -> uiListener?.handleSuccess(runningData())
            TypeFit.CYCLING -> uiListener?.handleSuccess(cyclingData())
        }
    }

    fun walkingData() = tracks.filter { it.typeFit == TypeFit.WALKING }
    fun runningData() = tracks.filter { it.typeFit == TypeFit.RUNNING }
    fun cyclingData() = tracks.filter { it.typeFit == TypeFit.CYCLING }

    inline fun <reified T> DataSnapshot.toModelOfType() = getValue(T::class.java)

    companion object {
        private var dataManager: TracksDataManager? = null

        val instance: TracksDataManager
            get() {
                if (dataManager == null) {
                    dataManager = TracksDataManager()
                }
                return dataManager as TracksDataManager
            }
    }

}
