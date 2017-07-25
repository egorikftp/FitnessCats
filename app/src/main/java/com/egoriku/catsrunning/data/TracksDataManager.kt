package com.egoriku.catsrunning.data

import com.egoriku.catsrunning.data.commons.TracksModel
import com.egoriku.catsrunning.helpers.TypeFit
import com.egoriku.catsrunning.models.Constants
import com.egoriku.catsrunning.utils.FirebaseUtils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class TracksDataManager private constructor() : ChildEventListener {

    private val tracks: MutableList<TracksModel> = mutableListOf()
    private var uiListener: UIListener? = null
    private var typeFit: Int = 0
    private val firebaseUtils: FirebaseUtils = FirebaseUtils.getInstance()
    private val databaseReference: DatabaseReference = firebaseUtils.firebaseDatabase
            .child(Constants.FirebaseFields.TRACKS)
            .child(firebaseUtils.user.uid)

    override fun onCancelled(databaseError: DatabaseError?) {
        uiListener?.handleError()
    }

    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {
        val value = dataSnapshot?.toModelOfType<TracksModel>()

        for (i in tracks.indices) {
            if (tracks[i].trackToken == value?.trackToken) {
                tracks[i] = value as TracksModel
                break
            }
        }
    }

    override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
        val value = dataSnapshot?.toModelOfType<TracksModel>()
        if (value != null) {
            tracks.add(value)
            notifySuccess(typeFit)
        }
    }

    override fun onChildRemoved(dataSnapshot: DataSnapshot?) {
        val value = dataSnapshot?.toModelOfType<TracksModel>()
        for (i in tracks.indices) {
            if (tracks[i].trackToken == value?.trackToken) {
                tracks.removeAt(i)
                break
            }
        }
        notifySuccess(typeFit)
    }

    fun addListener(listener: UIListener) {
        uiListener = listener
    }

    fun removeUIListener() {
        uiListener = null
    }

    fun clearData() {
        tracks.clear()
        databaseReference.removeEventListener(this)
    }

    fun close() {
        dataManager = null
    }

    fun loadTracks(typeFit: Int) {
        if (tracks.isEmpty() && this.typeFit == 0) {
            this.typeFit = typeFit
            databaseReference.addChildEventListener(this)
        } else {
            this.typeFit = typeFit
            notifySuccess(typeFit)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    inline private fun notifySuccess(typeFit: Int) {
        when (typeFit) {
            TypeFit.WALKING -> uiListener?.handleSuccess(walkingData())
            TypeFit.RUNNING -> uiListener?.handleSuccess(runningData())
            TypeFit.CYCLING -> uiListener?.handleSuccess(cyclingData())
        }
    }

    fun walkingData() = tracks.filter { it.typeFit == TypeFit.WALKING }.sortedByDescending { it.beginsAt }
    fun runningData() = tracks.filter { it.typeFit == TypeFit.RUNNING }.sortedByDescending { it.beginsAt }
    fun cyclingData() = tracks.filter { it.typeFit == TypeFit.CYCLING }.sortedByDescending { it.beginsAt }

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
