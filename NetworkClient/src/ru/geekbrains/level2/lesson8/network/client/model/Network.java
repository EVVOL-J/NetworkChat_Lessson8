package ru.geekbrains.level2.lesson8.network.client.model;

import javafx.application.Platform;

import ru.geekbrains.level2.lesson8.network.client.controller.ViewController;
import ru.geekbrains.level2.lesson8.network.clientserver.Command;
import ru.geekbrains.level2.lesson8.network.clientserver.commands.*;

import java.io.*;
import java.net.Socket;

public class Network {


    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8189;

    private final String host;
    private final int port;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;
    private String username;

    public Network() {
        this(SERVER_ADDRESS, SERVER_PORT);
    }

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            return true;
        } catch (IOException e) {
            System.err.println("Соединение не было установлено!");
            e.printStackTrace();
            return false;
        }
    }

    public String sendAuthCommand(String login, String password) {
        try {
            Command authCommand=Command.authCommand(login,password);
            sendCommand(authCommand);
            Command command=readCommand();
            if(command==null){
               return "Failed to read command from server";
            }
            switch (command.getType()){
                case AUTH_OK:{
                    AuthOkCommandData data= (AuthOkCommandData) command.getData();
                    this.username=data.getUsername();
                    return null;
                }
                case AUTH_ERROR:{
                    AuthErrorCommandData data= (AuthErrorCommandData) command.getData();
                    return data.getErrorMessage();
                }
                default:
                    return "Unknown type of command from Server"+ command.getType();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public void sendMessage(String message) throws IOException {
        Command command=Command.publicMessageCommand(username,message);
        sendCommand(command);
    }
    private void sendCommand(Command command) throws IOException {
        outputStream.writeObject(command);
    }

    public void sendPrivateMessage(String message, String recipient) throws IOException {
        Command command=Command.privateMessageCommand(recipient,message);
        sendCommand(command);
    }

    public void waitMessages(ViewController viewController) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Command command=readCommand();
                        if(command==null){
                            viewController.showError("Server error","Invalid command data");
                            continue;
                        }
                        switch (command.getType()){
                            case INFO_MESSAGE:{
                                MessageInfoCommandData data= (MessageInfoCommandData) command.getData();
                                String message=data.getMessage();
                                String sender=data.getSender();
                                String formattedMessage=(sender!=null) ? String.format("%s: %s", sender, message) : message;
                                Platform.runLater(() -> {
                                    viewController.appendMessage(formattedMessage);
                                });
                                break;
                            }
                            case ERROR:{
                                ErrorCommandData data= (ErrorCommandData) command.getData();
                                String errorMessage=data.getErrorMessage();
                                Platform.runLater(() -> {
                                    viewController.showError("Server error", errorMessage);
                                });
                            break;}
                            case UPDATE_USER_LIST:{
                                UpdateUserListCommandData data= (UpdateUserListCommandData) command.getData();
                                Platform.runLater(()->{
                                    viewController.updateUsersList(data.getUsers());
                                });
                                break;

                            }
                            default:{
                                Platform.runLater(() -> {
                                    viewController.showError("Unknown command from server!", command.getType().toString());
                                });
                            }
                        }

                    }
                } catch (IOException e) {
                    System.out.println("Соединение было потеряно!");
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    private Command readCommand() throws IOException {
        try {
            Command command = (Command) inputStream.readObject();
            return command;
        } catch (ClassNotFoundException e) {
            String errorMessage = "Unknown type of object from client";
            System.err.println(errorMessage);
            e.printStackTrace();
            return null;

        }
    }
}