package ru.geekbrains.level2.lesson8.network.server.chat.auth;

public interface AuthService {

    public void start();

    public String getUserNameByLoginAndPassword(String login,String password);
    public void stop();
}
