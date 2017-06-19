package com.egoriku.catsrunning.models.Firebase;

import android.os.Parcel;
import android.os.Parcelable;

import com.egoriku.catsrunning.helpers.TypeFit;

import java.util.ArrayList;
import java.util.List;

public class SaveModel implements Parcelable {
    private long beginsAt;
    private long time;
    private int distance;
    private int calories;
    private String trackToken;
    private boolean isFavorite;
    @TypeFit
    private int typeFit;
    private List<Point> points;

    public SaveModel() {
    }

    public SaveModel(long beginsAt, long time, int distance, String trackToken, int typeFit, List<Point> points) {
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

    public void setBeginsAt(long beginsAt) {
        this.beginsAt = beginsAt;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getTrackToken() {
        return trackToken;
    }

    public void setTrackToken(String trackToken) {
        this.trackToken = trackToken;
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

    public void setTypeFit(int typeFit) {
        this.typeFit = typeFit;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "SaveModel{" +
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

    protected SaveModel(Parcel in) {
        this.beginsAt = in.readLong();
        this.time = in.readLong();
        this.distance = in.readInt();
        this.calories = in.readInt();
        this.trackToken = in.readString();
        this.isFavorite = in.readByte() != 0;
        this.typeFit = in.readInt();
        this.points = new ArrayList<>();
        in.readList(this.points, Point.class.getClassLoader());
    }

    public static final Parcelable.Creator<SaveModel> CREATOR = new Parcelable.Creator<SaveModel>() {
        @Override
        public SaveModel createFromParcel(Parcel source) {
            return new SaveModel(source);
        }

        @Override
        public SaveModel[] newArray(int size) {
            return new SaveModel[size];
        }
    };
}
