package com.egoriku.catsrunning.models.Register;


public class RegistrationAfterRequestModel {
    private String status;
    private String token;
    private String code;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public RegistrationAfterRequestModel() {

    }
}
