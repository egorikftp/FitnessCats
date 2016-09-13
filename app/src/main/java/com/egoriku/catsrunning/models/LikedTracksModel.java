package com.egoriku.catsrunning.models;

public class LikedTracksModel {

    private int date;
    private int timeRunning;
    private int distance;
    private int id;
    private int liked;

    public LikedTracksModel() {
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getTimeRunning() {
        return timeRunning;
    }

    public void setTimeRunning(int timeRunning) {
        this.timeRunning = timeRunning;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
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
}
