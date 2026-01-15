package com.gestionpharmacie;

public class User {
    private String privelage;
    private String login;
    private String password;

    User(String login, String password) {
        this.login = login;
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public String getLogin() {
        return login;
    }
}
