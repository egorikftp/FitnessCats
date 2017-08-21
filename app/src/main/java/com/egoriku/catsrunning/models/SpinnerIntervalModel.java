package com.egoriku.catsrunning.models;

public class SpinnerIntervalModel {
    private String nameInterval;
    private long valueInterval;

    public SpinnerIntervalModel(String nameInterval, long valueInterval) {
        this.nameInterval = nameInterval;
        this.valueInterval = valueInterval;
    }

    public String getNameInterval() {
        return nameInterval;
    }

    public void setNameInterval(String nameInterval) {
        this.nameInterval = nameInterval;
    }

    public long getValueInterval() {
        return valueInterval;
    }

    public void setValueInterval(long valueInterval) {
        this.valueInterval = valueInterval;
    }
}
