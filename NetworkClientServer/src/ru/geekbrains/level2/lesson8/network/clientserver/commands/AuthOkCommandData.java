package ru.geekbrains.level2.lesson8.network.clientserver.commands;

import java.io.Serializable;

public class AuthOkCommandData implements Serializable {
    private final String username;


    public AuthOkCommandData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
