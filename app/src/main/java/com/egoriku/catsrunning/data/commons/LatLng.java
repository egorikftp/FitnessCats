package com.egoriku.catsrunning.data.commons;

import android.os.Parcel;
import android.os.Parcelable;

public class LatLng implements Parcelable {
    private double lng;
    private double lat;

    public LatLng() {
    }

    public LatLng(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lng);
        dest.writeDouble(this.lat);
    }

    protected LatLng(Parcel in) {
        this.lng = in.readDouble();
        this.lat = in.readDouble();
    }

    public static final Parcelable.Creator<LatLng> CREATOR = new Parcelable.Creator<LatLng>() {
        @Override
        public LatLng createFromParcel(Parcel source) {
            return new LatLng(source);
        }

        @Override
        public LatLng[] newArray(int size) {
            return new LatLng[size];
        }
    };
}