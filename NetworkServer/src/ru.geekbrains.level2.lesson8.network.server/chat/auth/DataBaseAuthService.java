package ru.geekbrains.level2.lesson8.network.server.chat.auth;


import ru.geekbrains.level2.lesson8.network.server.chat.database.DataBaseController;

import java.io.Serializable;
import java.sql.SQLException;

public class DataBaseAuthService implements AuthService, Serializable {
    private DataBaseController dataBaseController;

    public DataBaseController getDataBaseController() {
        return dataBaseController;
    }

    @Override
    public void start() {
        try {
            dataBaseController = new DataBaseController();
            dataBaseController.createDB();
            dataBaseController.readDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Ошибка подключения к базе данных");
        }

    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        if (login != null && password != null)
            return dataBaseController.getUserName(login, password);
        return null;
    }

    @Override
    public void stop() {
        try {
            dataBaseController.closeDB();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Ошибка закрытия базы данных");
        }

    }

    public boolean changeUserName(String username, String newUserName, String password) {
        return false;
    }
}
