package com.egoriku.catsrunning.models.PointsRequests;

import com.egoriku.catsrunning.models.Point;

import java.util.ArrayList;

public class PointsAfterRequestModel {
    private String status;
    private String code;
    private ArrayList<Point> points;

    public PointsAfterRequestModel() {
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
