package ru.geekbrains.level2.lesson8.network.clientserver.commands;

import java.io.Serializable;

public class AuthErrorCommandData implements Serializable {
    private final String errorMessage;

    public AuthErrorCommandData(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
