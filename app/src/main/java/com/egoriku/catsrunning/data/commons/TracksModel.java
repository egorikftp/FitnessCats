package com.egoriku.catsrunning.data.commons;

import android.os.Parcel;
import android.os.Parcelable;

import com.egoriku.catsrunning.helpers.TypeFit;

import java.util.ArrayList;
import java.util.List;

public class TracksModel implements Parcelable {

    private long beginsAt;
    private long time;
    private int distance;
    private int calories;
    private String trackToken;
    private boolean isFavorite;
    @TypeFit
    private int typeFit;
    private List<LatLng> points;

    public TracksModel() {
    }

    public TracksModel(long beginsAt, long time, int distance, String trackToken, int typeFit, List<LatLng> points) {
        this.beginsAt = beginsAt;
        this.time = time;
        this.distance = distance;
        this.points = points;
        this.trackToken = trackToken;
        this.typeFit = typeFit;
    }

    public long getBeginsAt() {
        return beginsAt;
    }

    public long getTime() {
        return time;
    }

    public int getDistance() {
        return distance;
    }

    public int getCalories() {
        return calories;
    }

    public String getTrackToken() {
        return trackToken;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getTypeFit() {
        return typeFit;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return "TracksModel{" +
                "beginsAt=" + beginsAt +
                ", time=" + time +
                ", distance=" + distance +
                ", calories=" + calories +
                ", trackToken=" + trackToken +
                ", isFavorite=" + isFavorite +
                ", typeFit=" + typeFit +
                ", points=" + points +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.beginsAt);
        dest.writeLong(this.time);
        dest.writeInt(this.distance);
        dest.writeInt(this.calories);
        dest.writeString(this.trackToken);
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
        dest.writeInt(this.typeFit);
        dest.writeList(this.points);
    }

    protected TracksModel(Parcel in) {
        this.beginsAt = in.readLong();
        this.time = in.readLong();
        this.distance = in.readInt();
        this.calories = in.readInt();
        this.trackToken = in.readString();
        this.isFavorite = in.readByte() != 0;
        this.typeFit = in.readInt();
        this.points = new ArrayList<>();
        in.readList(this.points, LatLng.class.getClassLoader());
    }

    public static final Parcelable.Creator<TracksModel> CREATOR = new Parcelable.Creator<TracksModel>() {
        @Override
        public TracksModel createFromParcel(Parcel source) {
            return new TracksModel(source);
        }

        @Override
        public TracksModel[] newArray(int size) {
            return new TracksModel[size];
        }
    };
}
