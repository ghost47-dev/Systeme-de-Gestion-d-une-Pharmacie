package com.gestionpharmacie.model;

public class User {
    private String login;
    private String password;
    private String privelage;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivelage() {
        return privelage;
    }

    public void setPrivelage(String privelage) {
        this.privelage = privelage;
    }
}
