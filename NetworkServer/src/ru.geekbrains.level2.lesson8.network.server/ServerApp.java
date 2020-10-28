package ru.geekbrains.level2.lesson8.network.server;

import ru.geekbrains.level2.lesson8.network.server.chat.MyServer;

import java.io.IOException;

public class ServerApp {
    private static final int DEFAULT_PORT = 8189;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }
        try {
            new MyServer(port).start();
        } catch (IOException e) {
            System.err.println("Failed to create MyServer");
            e.printStackTrace();
            System.exit(1);
        }
    }

}
