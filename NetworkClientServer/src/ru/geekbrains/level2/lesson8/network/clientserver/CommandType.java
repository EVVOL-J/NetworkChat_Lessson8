package ru.geekbrains.level2.lesson8.network.clientserver;

public enum CommandType {
    AUTH,
    AUTH_ERROR,
    AUTH_OK,
    PRIVATE_MESSAGE,
    PUBLIC_MESSAGE,
    INFO_MESSAGE,
    ERROR,
    END,
    UPDATE_USER_LIST,
    CHANGE_USER_NAME,
    CHANGE_USER_NAME_OK,

}
