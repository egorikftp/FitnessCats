package com.egoriku.catsrunning.models.Firebase

import android.os.Parcel
import android.os.Parcelable

class Point : Parcelable {
    var lng: Double = 0.toDouble()
    var lat: Double = 0.toDouble()

    constructor() {}

    constructor(lng: Double, lat: Double) {
        this.lng = lng
        this.lat = lat
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeDouble(this.lng)
        dest.writeDouble(this.lat)
    }

    protected constructor(`in`: Parcel) {
        this.lng = `in`.readDouble()
        this.lat = `in`.readDouble()
    }

    companion object {

        val CREATOR: Parcelable.Creator<Point> = object : Parcelable.Creator<Point> {
            override fun createFromParcel(source: Parcel): Point {
                return Point(source)
            }

            override fun newArray(size: Int): Array<Point> {
                return arrayOfNulls(size)
            }
        }
    }
}
