package com.egoriku.catsrunning.models;

public class ReminderModel {

    public ReminderModel() {
    }

    private int id;
    private int dateReminder;
    private String textReminder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDateReminder() {
        return dateReminder;
    }

    public void setDateReminder(int dateReminder) {
        this.dateReminder = dateReminder;
    }

    public String getTextReminder() {
        return textReminder;
    }

    public void setTextReminder(String textReminder) {
        this.textReminder = textReminder;
    }
}
