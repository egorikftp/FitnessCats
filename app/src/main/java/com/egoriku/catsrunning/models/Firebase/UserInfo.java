package com.egoriku.catsrunning.models.Firebase;

public class UserInfo {

    private int age;
    private int growth;
    private int weight;

    public UserInfo() {
    }

    public UserInfo(int age, int growth, int weight) {
        this.age = age;
        this.growth = growth;
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGrowth() {
        return growth;
    }

    public void setGrowth(int growth) {
        this.growth = growth;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
