package com.egoriku.catsrunning.models;

public class ReminderModel {

    private int id;
    private long dateReminder;
    private int typeReminder;

    private int isRing;

    public ReminderModel() {
    }

    public ReminderModel(int id, long dateReminder, int typeReminder, int isRing) {
        this.id = id;
        this.dateReminder = dateReminder;
        this.typeReminder = typeReminder;
        this.isRing = isRing;
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

    public int getIsRing() {
        return isRing;
    }

    public void setIsRing(int isRing) {
        this.isRing = isRing;
    }
}
