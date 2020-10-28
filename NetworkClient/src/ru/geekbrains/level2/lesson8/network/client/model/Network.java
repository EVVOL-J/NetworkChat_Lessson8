package ru.geekbrains.level2.lesson8.network.client.model;

import javafx.application.Platform;
import ru.geekbrains.level2.lesson8.network.client.controller.ViewController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8189;
    public static final String AUTH_CMD_PREFIX = "/auth";
    private final String AUTHOK_CMD_PREFIX="/authok";
    private final String AUTHERR_CMD_PREFIX="/autherr";
    private final String host;
    private final int port;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket socket;
    private String userName;

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
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            System.err.println("Соединение не было установлено!");
            e.printStackTrace();
            return false;
        }
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public void waitMessages(ViewController viewController) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String message = inputStream.readUTF();
                        Platform.runLater(() -> {
                            viewController.appendMessage("Сервер: " + message);
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Соединение было потеряно!");
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public String sendAuthCommand(String login,String password) {
        try {
            outputStream.writeUTF(String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));
            String response=getInputStream().readUTF();
            if(response.startsWith(AUTHOK_CMD_PREFIX)){
                this.userName=response.split("\\s+",2)[1];
                return null;
            }
            else return response.split("\\s+",2)[1];
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }
}