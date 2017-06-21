package com.egoriku.catsrunning.ui.commons

import com.egoriku.catsrunning.data.TracksModel

interface UIListener {

    fun handleError()
    fun handleSuccess(data: List<TracksModel>)
}
