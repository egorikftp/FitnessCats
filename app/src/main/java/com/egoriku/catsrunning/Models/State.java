package com.egoriku.catsrunning.models;

public class State {
    /**
     * task логина/регистрация
     */
    private boolean isStartTaskAuthentification;

    /**
     * данные сервиса пробежки
     */
    private long sinceTime;
    private int nowDistance;


    public boolean isStartTaskAuthentification() {
        return isStartTaskAuthentification;
    }

    public void setStartTaskAuthentification(boolean startTaskAuthentification) {
        isStartTaskAuthentification = startTaskAuthentification;
    }

    public long getSinceTime() {
        return sinceTime;
    }

    public void setSinceTime(long sinceTime) {
        this.sinceTime = sinceTime;
    }

    public String getNowDistance() {
        return String.valueOf(nowDistance);
    }

    public void setNowDistance(int nowDistance) {
        this.nowDistance = nowDistance;
    }
}
