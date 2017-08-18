package com.egoriku.catsrunning.data

import com.egoriku.catsrunning.data.commons.TracksModel
import com.egoriku.catsrunning.helpers.TypeFit
import com.egoriku.catsrunning.models.Constants
import com.egoriku.catsrunning.utils.FirebaseUtils
import com.egoriku.core_lib.extensions.toModelOfType
import com.google.firebase.database.*

class TracksDataManager private constructor() : ChildEventListener, ValueEventListener {

    private val tracks: MutableList<TracksModel> = mutableListOf()
    private var uiListener: UIListener? = null
    @TypeFit
    private var typeFit: Int = TypeFit.UNCERTAIN
    private val firebaseUtils: FirebaseUtils = FirebaseUtils.getInstance()
    private val databaseReference: DatabaseReference = firebaseUtils.firebaseDatabase
            .child(Constants.FirebaseFields.TRACKS)
            .child(firebaseUtils.user.uid)

    override fun onDataChange(p0: DataSnapshot?) {
        notifySuccess(typeFit)
    }

    override fun onCancelled(databaseError: DatabaseError?) {
        uiListener?.handleError()
    }

    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {
        dataSnapshot?.toModelOfType<TracksModel>()?.let {
            for (i in tracks.indices) {
                if (tracks[i].trackToken == it.trackToken) {
                    tracks[i] = it
                    break
                }
            }
        }
    }

    override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
        dataSnapshot?.toModelOfType<TracksModel>()?.let {
            tracks.add(it)
        }
    }

    override fun onChildRemoved(dataSnapshot: DataSnapshot?) {
        dataSnapshot?.toModelOfType<TracksModel>()?.let {
            for (i in tracks.indices) {
                if (tracks[i].trackToken == it.trackToken) {
                    tracks.removeAt(i)
                    break
                }
            }
        }

        notifySuccess(typeFit)
    }

    fun addListener(listener: UIListener) {
        uiListener = listener
    }

    fun clearData() {
        tracks.clear()
        removeListeners()
    }

    fun removeListeners() {
        databaseReference.removeEventListener(this as ChildEventListener)
        databaseReference.removeEventListener(this as ValueEventListener)
        uiListener = null
    }

    fun close() {
        dataManager = null
    }

    fun loadTracks(typeFit: Int) {
        if (tracks.isEmpty() && this.typeFit == TypeFit.UNCERTAIN) {
            this.typeFit = typeFit
            databaseReference.addChildEventListener(this)
            databaseReference.addListenerForSingleValueEvent(this)
        } else {
            this.typeFit = typeFit
            notifySuccess(this.typeFit)
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

    private fun walkingData() = tracks.filter { it.typeFit == TypeFit.WALKING }.sortedByDescending { it.beginsAt }
    private fun runningData() = tracks.filter { it.typeFit == TypeFit.RUNNING }.sortedByDescending { it.beginsAt }
    private fun cyclingData() = tracks.filter { it.typeFit == TypeFit.CYCLING }.sortedByDescending { it.beginsAt }

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
