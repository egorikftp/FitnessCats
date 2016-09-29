package com.egoriku.catsrunning.models.Firebase;

import java.util.ArrayList;

public class SaveModel {
    private long beginsAt;
    private long time;
    private int distance;
    private ArrayList<Point> points;

    public SaveModel() {
    }

    public SaveModel(long beginsAt, long time, int distance, ArrayList<Point> points) {
        this.beginsAt = beginsAt;
        this.time = time;
        this.distance = distance;
        this.points = points;
    }


   /* public Map<String, Object> toMap(){
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("beginsAt", beginsAt);
        resultMap.put("time", time);
        resultMap.put("distance", distance);
        resultMap.put("points", points);

        return resultMap;
    }*/

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
}
