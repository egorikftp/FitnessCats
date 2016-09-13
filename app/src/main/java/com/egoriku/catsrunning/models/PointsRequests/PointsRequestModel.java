package com.egoriku.catsrunning.models.PointsRequests;

public class PointsRequestModel {

    private String token;
    private int id;

    public PointsRequestModel(String token, int id) {
        this.token = token;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
