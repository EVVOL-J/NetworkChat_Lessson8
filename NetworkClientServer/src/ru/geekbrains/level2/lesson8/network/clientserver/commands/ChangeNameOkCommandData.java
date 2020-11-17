package ru.geekbrains.level2.lesson8.network.clientserver.commands;

import java.io.Serializable;

public class ChangeNameOkCommandData implements Serializable {
    private final String newUsername;


    public ChangeNameOkCommandData(String username) {
        this.newUsername = username;
    }

    public String getUsername() {
        return newUsername;
    }
}