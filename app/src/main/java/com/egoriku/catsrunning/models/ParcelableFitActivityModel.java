package com.egoriku.catsrunning.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableFitActivityModel implements Parcelable {
    private int btnStart;
    private int btnFinish;
    private int textTimeFitVisibility;
    private int textDistanceVisibility;
    private int imageViewFinish;
    private int textTimerVisibility;
    private String toolbarText;
    private String textDistance;
    private String textTimer;
    private String textTimeFit;
    private boolean isChronometerRun;
    private long chromonometerStartTime;


    public ParcelableFitActivityModel() {
    }


    public static final Creator<ParcelableFitActivityModel> CREATOR = new Creator<ParcelableFitActivityModel>() {
        @Override
        public ParcelableFitActivityModel createFromParcel(Parcel in) {
            return new ParcelableFitActivityModel();
        }

        @Override
        public ParcelableFitActivityModel[] newArray(int size) {
            return new ParcelableFitActivityModel[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(btnStart);
        parcel.writeInt(btnFinish);
        parcel.writeInt(textTimeFitVisibility);
        parcel.writeInt(textDistanceVisibility);
        parcel.writeInt(imageViewFinish);
        parcel.writeInt(textTimerVisibility);
        parcel.writeString(toolbarText);
        parcel.writeString(textDistance);
        parcel.writeString(textTimer);
    }


    public long getChromonometerStartTime() {
        return chromonometerStartTime;
    }

    public void setChromonometerStartTime(long chromonometerStartTime) {
        this.chromonometerStartTime = chromonometerStartTime;
    }

    public boolean isChronometerRun() {
        return isChronometerRun;
    }

    public void setChronometerRun(boolean chronometerRun) {
        isChronometerRun = chronometerRun;
    }

    public String getTextTimeFit() {
        return textTimeFit;
    }

    public void setTextTimeFit(String textTimeFit) {
        this.textTimeFit = textTimeFit;
    }

    public int getBtnStart() {
        return btnStart;
    }

    public void setBtnStart(int btnStart) {
        this.btnStart = btnStart;
    }

    public int getBtnFinish() {
        return btnFinish;
    }

    public void setBtnFinish(int btnFinish) {
        this.btnFinish = btnFinish;
    }

    public int getTextTimeFitVisibility() {
        return textTimeFitVisibility;
    }

    public void setTextTimeFitVisibility(int textTimeFitVisibility) {
        this.textTimeFitVisibility = textTimeFitVisibility;
    }

    public int getTextDistanceVisibility() {
        return textDistanceVisibility;
    }

    public void setTextDistanceVisibility(int textDistanceVisibility) {
        this.textDistanceVisibility = textDistanceVisibility;
    }

    public int getImageViewFinish() {
        return imageViewFinish;
    }

    public void setImageViewFinish(int imageViewFinish) {
        this.imageViewFinish = imageViewFinish;
    }

    public int getTextTimerVisibility() {
        return textTimerVisibility;
    }

    public void setTextTimerVisibility(int textTimerVisibility) {
        this.textTimerVisibility = textTimerVisibility;
    }

    public String getToolbarText() {
        return toolbarText;
    }

    public void setToolbarText(String toolbarText) {
        this.toolbarText = toolbarText;
    }

    public String getTextDistance() {
        return textDistance;
    }

    public void setTextDistance(String textDistance) {
        this.textDistance = textDistance;
    }

    public String getTextTimer() {
        return textTimer;
    }

    public void setTextTimer(String textTimer) {
        this.textTimer = textTimer;
    }
}
