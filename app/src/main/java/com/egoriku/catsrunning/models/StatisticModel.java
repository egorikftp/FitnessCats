package com.egoriku.catsrunning.models;

public class StatisticModel {
    private int fitDistance;

    public StatisticModel(int fitDistance) {
        this.fitDistance = fitDistance;
    }

    public int getFitDistance() {
        return fitDistance;
    }

    public void setFitDistance(int fitDistance) {
        this.fitDistance = fitDistance;
    }
}
