package ru.geekbrains.level2.lesson8.network.server.chat.handler;

import ru.geekbrains.level2.lesson8.network.clientserver.Command;
import ru.geekbrains.level2.lesson8.network.clientserver.CommandType;
import ru.geekbrains.level2.lesson8.network.clientserver.commands.AuthCommandData;
import ru.geekbrains.level2.lesson8.network.clientserver.commands.ChangeUserNameCommandData;
import ru.geekbrains.level2.lesson8.network.clientserver.commands.PrivateMessageCommandData;
import ru.geekbrains.level2.lesson8.network.clientserver.commands.PublicMessageCommandData;
import ru.geekbrains.level2.lesson8.network.server.chat.MyServer;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler {


    private static final long DELAY_CONNECTION = 120000L;
    private final MyServer myServer;
    private final Socket clientSocket;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String username;

    public ClientHandler(MyServer myServer, Socket clientSocket) {
        this.myServer = myServer;
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());

        new Thread(() -> {
            try {
                authentication();
                readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    closeConnection();
                } catch (IOException e) {
                    System.err.println("Failed to close connection!");
                }
            }
        }).start();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private void readMessages() throws IOException {
        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }
            switch (command.getType()) {
                case END:
                    return;
                case PRIVATE_MESSAGE: {
                    PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
                    String recipient = data.getReceiver();
                    String privateMessage = data.getMessage();
                    myServer.sendPrivateMessage(recipient, Command.messageInfoCommand(username, privateMessage));
                    break;
                }
                case PUBLIC_MESSAGE: {
                    PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
                    String publicMessage = data.getMessage();
                    String sender = data.getSender();
                    myServer.broadcastMessage(this, Command.messageInfoCommand(sender, publicMessage));
                    break;
                }

                case CHANGE_USER_NAME: {
                    ChangeUserNameCommandData data = (ChangeUserNameCommandData) command.getData();
                    String username = data.getUsername();
                    String newUserName = data.getNewUserName();
                    String password = data.getPassword();
                    if (myServer.updateUserList(newUserName, password, this)) {
                        myServer.broadcastMessage(null, Command.messageInfoCommand(username, "сменил имя на " + newUserName));
                    } else sendMessage(Command.errorCommand("Не удалось сменит имя пользователя неверный пароль"));

                    break;
                }

                default:
                    System.err.println("Unknown type of command: " + command.getType());


            }

        }
    }

    private Command readCommand() throws IOException {
        try {
            Command command = (Command) in.readObject();
            return command;
        } catch (ClassNotFoundException e) {
            String errorMessage = "Unknown type of object from client";
            System.err.println(errorMessage);
            e.printStackTrace();
            sendMessage(Command.authErrorCommand(errorMessage));
            return null;

        }
    }

    private void authentication() throws IOException {
        System.out.println("Сервер был запущен");
        Timer timer = new Timer();
        TimerTask timerTask = timerTask();

        timer.schedule(timerTask, DELAY_CONNECTION);

        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }
            if (command.getType() == CommandType.AUTH) {
                boolean isSuccessAuth = processAuthCommand(command);
                if (isSuccessAuth) {
                    break;
                }
            } else {
                sendMessage(Command.authErrorCommand("auth command is required!"));
            }
        }

    }

    private TimerTask timerTask() {

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (username == null) {
                    try {
                        System.out.println("Время подключения истекло");
                        sendMessage(Command.authErrorCommand("Время подключения истекло"));
                        closeConnection();
                    } catch (IOException e) {
                        System.err.println("Ошибка посылки комманды времени подключения");
                        e.printStackTrace();
                    }
                }
            }
        };
        return timerTask;
    }

    private boolean processAuthCommand(Command command) throws IOException {
        AuthCommandData authCommandData = (AuthCommandData) command.getData();
        String login = authCommandData.getLogin();
        String password = authCommandData.getPassword();
        this.username = myServer.getAuthService().getUsernameByLoginAndPassword(login, password);
        if (username != null) {
            if (myServer.isNicknameAlreadyBusy(username)) {
                sendMessage(Command.authErrorCommand("Login and password are already used!"));
                return false;
            }
            sendMessage(Command.authOkCommand(username));
            String message = username + " joined to chat!";
            myServer.subscribe(this);
            myServer.broadcastMessage(this, Command.messageInfoCommand(null, message));
            return true;
        } else {
            sendMessage(Command.authErrorCommand("Login and/or password are invalid! Please, try again"));
            return false;
        }
    }


    private void closeConnection() throws IOException {
        if (username != null) myServer.unsubscribe(this);
        clientSocket.close();
    }


    public void sendMessage(Command command) throws IOException {
        out.writeObject(command);
    }
}