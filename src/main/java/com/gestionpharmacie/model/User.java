package com.gestionpharmacie.model;

public class User {
    private String login;
    private String password;
    private String privilege;

    public User(String login, String password, String privilege){
        this.login = login;
        this.password = password;
        this.privilege = privilege;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }


    public String getPrivilege() {
        return privilege;
    }
}

