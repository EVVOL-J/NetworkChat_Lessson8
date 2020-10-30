package ru.geekbrains.level2.lesson8.network.server.chat;

import ru.geekbrains.level2.lesson8.network.clientserver.Command;
import ru.geekbrains.level2.lesson8.network.server.chat.auth.AuthService;
import ru.geekbrains.level2.lesson8.network.server.chat.auth.BaseAuthService;
import ru.geekbrains.level2.lesson8.network.server.chat.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MyServer {


    private final ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final AuthService authService;


    public MyServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.authService = new BaseAuthService();
    }

    public void start() throws IOException {

        authService.start();
        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            System.err.println("Failed to accept new connection");
            e.printStackTrace();
        } finally {
            authService.stop();
            serverSocket.close();
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
        System.out.println("Ожидание нового подключения....");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Клиент подключился");// /auth login password
        processClientConnection(clientSocket);
    }

    private void processClientConnection(Socket clientSocket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized void broadcastMessage(ClientHandler sender, Command command) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }

            client.sendMessage(command);
        }
    }

    public synchronized void subscribe(ClientHandler handler) throws IOException {

        clients.add(handler);
        List<String> usersNames=getAllUsersNames();
        broadcastMessage(null,Command.updateUserListCommand(usersNames));
    }

    private List<String> getAllUsersNames() {
        List<String> usernames= new ArrayList<>();
       for(ClientHandler client:clients)
       {
           usernames.add(client.getUsername());
       }
       return usernames;
    }

    public synchronized void unsubscribe(ClientHandler handler) throws IOException {
        clients.remove(handler);
        List<String> usersNames=getAllUsersNames();
        broadcastMessage(null,Command.updateUserListCommand(usersNames));
    }

    public synchronized boolean isNicknameAlreadyBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendPrivateMessage(String recipient, Command command) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendMessage(command);
            }
        }
    }
}