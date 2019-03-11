package com.xaau.bs.busx.domain;

public class UserInfo {
    private String email;
    private boolean isLogin;

    public UserInfo(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
