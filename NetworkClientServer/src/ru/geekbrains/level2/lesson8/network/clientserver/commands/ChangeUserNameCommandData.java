package ru.geekbrains.level2.lesson8.network.clientserver.commands;

import java.io.Serializable;

public class ChangeUserNameCommandData implements Serializable {
    private final String username;
    private final String newUserName;
    private final String password;

    public ChangeUserNameCommandData(String username, String newUserName, String password) {
        this.username = username;
        this.newUserName = newUserName;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getNewUserName() {
        return newUserName;
    }

    public String getPassword() {
        return password;
    }
}
