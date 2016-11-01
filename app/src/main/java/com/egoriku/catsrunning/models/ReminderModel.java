package com.egoriku.catsrunning.models;

public class ReminderModel {

    private int id;
    private long dateReminder;
    private String textReminder;

    public ReminderModel() {
    }

    public ReminderModel(int id, long dateReminder, String textReminder) {
        this.id = id;
        this.dateReminder = dateReminder;
        this.textReminder = textReminder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDateReminder() {
        return dateReminder;
    }

    public void setDateReminder(long dateReminder) {
        this.dateReminder = dateReminder;
    }

    public String getTextReminder() {
        return textReminder;
    }

    public void setTextReminder(String textReminder) {
        this.textReminder = textReminder;
    }
}
