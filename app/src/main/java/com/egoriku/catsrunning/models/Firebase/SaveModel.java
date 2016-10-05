package com.egoriku.catsrunning.models.Firebase;

import java.util.ArrayList;

public class SaveModel {
    private long beginsAt;
    private long time;
    private int distance;
    private String trackToken;
    private ArrayList<Point> points;

    public SaveModel() {
    }

    public SaveModel(long beginsAt, long time, int distance, String trackToken, ArrayList<Point> points) {
        this.beginsAt = beginsAt;
        this.time = time;
        this.distance = distance;
        this.points = points;
        this.trackToken = trackToken;
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

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public String getTrackToken() {
        return trackToken;
    }

    public void setTrackToken(String trackToken) {
        this.trackToken = trackToken;
    }
}
