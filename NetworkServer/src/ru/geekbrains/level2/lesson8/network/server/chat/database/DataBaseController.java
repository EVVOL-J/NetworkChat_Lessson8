package ru.geekbrains.level2.lesson8.network.server.chat.database;

import java.sql.*;

public class DataBaseController {
    private Connection conn;
    private Statement statement;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private final String sqlGetUsernameByLoginAndPassword = "SELECT username FROM users WHERE login=? and password=?";
    private final String sqlGetUsernameByPassword = "SELECT username FROM users WHERE password=?";
    private final String sqlSetNewUserName = "UPDATE 'users' SET username=? WHERE username=? and password=?";


    public DataBaseController() throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:CHAT.s3db");
    }

    public void createDB() throws SQLException {
        statement = conn.createStatement();
        preparedStatement = conn.prepareStatement(sqlGetUsernameByLoginAndPassword);

        // statement.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'login' text, 'password' text, 'username' text )");
    }

    public String getUserName(String login, String password) {
        if (login != null && password != null) {
            try {
                preparedStatement = conn.prepareStatement(sqlGetUsernameByLoginAndPassword);
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, password);
                resultSet = preparedStatement.executeQuery();
                return resultSet.getString("username");

            } catch (SQLException t) {
                return null;
            }

        }

        return null;
    }

    public boolean changeUserNameByPassword(String username, String newUserName, String password) {
        try {
            preparedStatement = conn.prepareStatement(sqlSetNewUserName);
            preparedStatement.setString(1, newUserName);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            if (preparedStatement.executeUpdate() == 1)
                return true;
            else return false;

        } catch (SQLException t) {
            return false;
        }
    }

    public void readDB() throws SQLException {
        resultSet = statement.executeQuery("SELECT * FROM users");

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String login = resultSet.getString("login");
            String password = resultSet.getString("password");
            String username = resultSet.getString("username");

            System.out.printf("%d %s %s %s \n", id, login, password, username);
        }

    }

    public void writeDB() throws SQLException {
        statement.execute("INSERT INTO 'users' ('login','password','username') VALUES ('login1','pass1','Oleg');");
        statement.execute("INSERT INTO 'users' ('login','password','username') VALUES ('login2','pass2','Nicolai');");
        statement.execute("INSERT INTO 'users' ('login','password','username') VALUES ('login3','pass3','Petya');");
        statement.execute("INSERT INTO 'users' ('login','password','username') VALUES ('login4','pass4','Vasya');");

    }

    public void closeDB() throws SQLException {
        resultSet.close();
        preparedStatement.close();
        statement.close();
        conn.close();
    }
}
