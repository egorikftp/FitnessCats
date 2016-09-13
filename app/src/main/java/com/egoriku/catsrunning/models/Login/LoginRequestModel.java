package com.egoriku.catsrunning.models.Login;

public class LoginRequestModel {
    private String email;
    private String password;

    public LoginRequestModel() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
