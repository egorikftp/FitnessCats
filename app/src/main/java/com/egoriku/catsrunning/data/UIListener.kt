package com.egoriku.catsrunning.data

import com.egoriku.catsrunning.data.commons.TracksModel

interface UIListener {

    fun handleError()

    fun handleSuccess(data: List<TracksModel>)
}
