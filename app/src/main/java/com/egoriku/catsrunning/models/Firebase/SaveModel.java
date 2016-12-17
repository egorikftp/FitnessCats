package com.egoriku.catsrunning.models.Firebase;

import java.util.List;

public class SaveModel {
    private long beginsAt;
    private long time;
    private int distance;
    private String trackToken;
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

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
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
