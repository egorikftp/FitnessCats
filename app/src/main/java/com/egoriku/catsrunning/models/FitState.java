package com.egoriku.catsrunning.models;

import com.egoriku.catsrunning.models.Firebase.Point;

import java.util.ArrayList;
import java.util.List;

public class FitState {
    private long sinceTime;
    private int nowDistance;
    private long startTime;
    private long idTrack;
    private boolean isFitRun;
    private List<Point> points = new ArrayList<>();
    private long timeBetweenLocations;
    private long weight;
    private double calories;
    private int typeFit;

    public int getTypeFit() {
        return typeFit;
    }

    public void setTypeFit(int typeFit) {
        this.typeFit = typeFit;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public long getTimeBetweenLocations() {
        return timeBetweenLocations;
    }

    public void setTimeBetweenLocations(long timeBetweenLocations) {
        this.timeBetweenLocations = timeBetweenLocations;
    }

    public boolean isFitRun() {
        return isFitRun;
    }

    public void setFitRun(boolean fitRun) {
        isFitRun = fitRun;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public long getIdTrack() {
        return idTrack;
    }

    public void setIdTrack(long idTrack) {
        this.idTrack = idTrack;
    }

    public long getSinceTime() {
        return sinceTime;
    }

    public void setSinceTime(long sinceTime) {
        this.sinceTime = sinceTime;
    }

    public void setNowDistance(int nowDistance) {
        this.nowDistance = nowDistance;
    }

    public float getNowDistance() {
        return nowDistance;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
