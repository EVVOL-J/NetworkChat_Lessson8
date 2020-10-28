package ru.geekbrains.level2.lesson8.network.server.chat.auth;

public interface AuthService {

    void start();

    String getUsernameByLoginAndPassword(String login, String password);

    void stop();
}
