package com.egoriku.catsrunning.models;

import com.egoriku.catsrunning.data.commons.LatLng;
import com.egoriku.catsrunning.helpers.TypeFit;

import java.util.List;

public class AllFitnessDataModel {

    public AllFitnessDataModel() {
    }

    private long beginsAt;
    private long time;
    private long distance;
    private int id;
    private int liked;
    @TypeFit
    private int typeFit;
    private String trackToken;
    private List<LatLng> latLngs;
    private double calories;

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
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

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    public String getTrackToken() {
        return trackToken;
    }

    public void setTrackToken(String trackToken) {
        this.trackToken = trackToken;
    }

    public int getTypeFit() {
        return typeFit;
    }

    public void setTypeFit(int typeFit) {
        this.typeFit = typeFit;
    }
}
