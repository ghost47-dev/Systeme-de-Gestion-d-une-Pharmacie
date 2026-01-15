package com.gestionpharmacie.managers;

import com.gestionpharmacie.model.User;

import java.util.ArrayList;

public class UserManager {
    private ArrayList<User> users = new ArrayList<>();

    public User fetchUser(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }
    public void addUser(User user) {
        users.add(user);
    }
}
