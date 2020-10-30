package ru.geekbrains.level2.lesson8.network.clientserver.commands;

import java.io.Serializable;

public class MessageInfoCommandData implements Serializable {
    private final String sender;
    private final String message;

    public  MessageInfoCommandData(String username, String message) {
        this.sender = username;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
