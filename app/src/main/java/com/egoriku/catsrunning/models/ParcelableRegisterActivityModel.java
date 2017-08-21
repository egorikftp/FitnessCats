package com.egoriku.catsrunning.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableRegisterActivityModel implements Parcelable{
    private int inputLayoutEmail;
    private int inputLayoutPassword;
    private int inputLayoutDoublePassword;
    private int inputLayoutName;
    private int inputLayoutSurname;
    private int signInGoogleBtn;
    private String toolbarText;
    private String registerBtn;
    private String loginBtn;


    public ParcelableRegisterActivityModel(int inputLayoutEmail, int inputLayoutPassword, int inputLayoutDoublePassword, int inputLayoutName, int inputLayoutSurname, int signInGoogleBtn, String toolbarText, String registerBtn, String loginBtn) {
        this.inputLayoutEmail = inputLayoutEmail;
        this.inputLayoutPassword = inputLayoutPassword;
        this.inputLayoutDoublePassword = inputLayoutDoublePassword;
        this.inputLayoutName = inputLayoutName;
        this.inputLayoutSurname = inputLayoutSurname;
        this.signInGoogleBtn = signInGoogleBtn;
        this.toolbarText = toolbarText;
        this.registerBtn = registerBtn;
        this.loginBtn = loginBtn;
    }


    public static final Creator<ParcelableRegisterActivityModel> CREATOR = new Creator<ParcelableRegisterActivityModel>() {
        @Override
        public ParcelableRegisterActivityModel createFromParcel(Parcel in) {
            return new ParcelableRegisterActivityModel(
                    in.readInt(), in.readInt(), in.readInt(),in.readInt(),in.readInt(),in.readInt(),in.readString(),in.readString(),in.readString());
        }

        @Override
        public ParcelableRegisterActivityModel[] newArray(int size) {
            return new ParcelableRegisterActivityModel[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(inputLayoutEmail);
        parcel.writeInt(inputLayoutPassword);
        parcel.writeInt(inputLayoutDoublePassword);
        parcel.writeInt(inputLayoutName);
        parcel.writeInt(inputLayoutSurname);
        parcel.writeInt(signInGoogleBtn);
        parcel.writeString(toolbarText);
        parcel.writeString(registerBtn);
        parcel.writeString(loginBtn);
    }


    public int getInputLayoutEmail() {
        return inputLayoutEmail;
    }

    public void setInputLayoutEmail(int inputLayoutEmail) {
        this.inputLayoutEmail = inputLayoutEmail;
    }

    public int getInputLayoutPassword() {
        return inputLayoutPassword;
    }

    public void setInputLayoutPassword(int inputLayoutPassword) {
        this.inputLayoutPassword = inputLayoutPassword;
    }

    public int getInputLayoutDoublePassword() {
        return inputLayoutDoublePassword;
    }

    public void setInputLayoutDoublePassword(int inputLayoutDoublePassword) {
        this.inputLayoutDoublePassword = inputLayoutDoublePassword;
    }

    public int getInputLayoutName() {
        return inputLayoutName;
    }

    public void setInputLayoutName(int inputLayoutName) {
        this.inputLayoutName = inputLayoutName;
    }

    public int getInputLayoutSurname() {
        return inputLayoutSurname;
    }

    public void setInputLayoutSurname(int inputLayoutSurname) {
        this.inputLayoutSurname = inputLayoutSurname;
    }

    public int getSignInGoogleBtn() {
        return signInGoogleBtn;
    }

    public void setSignInGoogleBtn(int signInGoogleBtn) {
        this.signInGoogleBtn = signInGoogleBtn;
    }

    public String getToolbarText() {
        return toolbarText;
    }

    public void setToolbarText(String toolbarText) {
        this.toolbarText = toolbarText;
    }

    public String getRegisterBtn() {
        return registerBtn;
    }

    public void setRegisterBtn(String registerBtn) {
        this.registerBtn = registerBtn;
    }

    public String getLoginBtn() {
        return loginBtn;
    }

    public void setLoginBtn(String loginBtn) {
        this.loginBtn = loginBtn;
    }
}
