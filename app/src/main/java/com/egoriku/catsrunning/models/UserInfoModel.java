package com.egoriku.catsrunning.models;

public class UserInfoModel {
    private long growth;
    private long weight;

    public UserInfoModel() {
    }

    public UserInfoModel(long growth, long weight) {
        this.growth = growth;
        this.weight = weight;
    }

    public long getGrowth() {
        return growth;
    }

    public void setGrowth(long growth) {
        this.growth = growth;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }
}
