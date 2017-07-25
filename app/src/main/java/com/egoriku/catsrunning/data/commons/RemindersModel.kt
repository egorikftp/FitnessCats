package com.egoriku.catsrunning.data.commons

class RemindersModel(val map: MutableMap<String, Any?>) {

    var _id: Long by map
    var date: Long by map
    var typeFit: Int by map
    var isRing: Int by map

    constructor(id: Long, date: Long, typeFit: Int, isRing: Int) : this(HashMap()) {
        this._id = id
        this.date = date
        this.typeFit = typeFit
        this.isRing = isRing
    }
}