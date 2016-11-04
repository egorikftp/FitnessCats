package com.egoriku.catsrunning.models;

public class ReminderModel {

    private int id;
    private long dateReminder;
    private int typeReminder;

    public ReminderModel() {
    }

    public ReminderModel(int id, long dateReminder, int typeReminder) {
        this.id = id;
        this.dateReminder = dateReminder;
        this.typeReminder = typeReminder;
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

    public int getTypeReminder() {
        return typeReminder;
    }

    public void setTypeReminder(int typeReminder) {
        this.typeReminder = typeReminder;
    }
}
