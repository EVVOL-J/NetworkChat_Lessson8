package ru.geekbrains.level2.lesson8.network.server.chat;

import ru.geekbrains.level2.lesson8.network.server.chat.auth.AuthService;
import ru.geekbrains.level2.lesson8.network.server.chat.auth.BatheAuthService;
import ru.geekbrains.level2.lesson8.network.server.chat.handler.ClintHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {


    private final ServerSocket serverSocket;
    private final List<ClintHandler> clients=new ArrayList<>();
    private AuthService authService;

    public MyServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.authService=new BatheAuthService();
    }

    public void start() throws IOException {
        System.out.println("Сервер был запущен");
        authService.start();
        try {
            while (true){
                waitNewClintConnection();
            }
        } catch (IOException e) {
            System.err.println("Failed to accept new connection");
            e.printStackTrace();
        }finally {
            authService.stop();
            serverSocket.close();
        }
    }

    private void waitNewClintConnection() throws IOException {
        System.out.println("Ожидание нового подключения....");
        Socket clintSocket=serverSocket.accept();
        System.out.println("Клиент подключен");
        processClientConnection(clintSocket);
        return;
    }

    private void processClientConnection(Socket clintSocket) throws IOException {
        ClintHandler clintHandler=new ClintHandler(this,clintSocket);
        clintHandler.handle();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized void broadcastMessage(String message, ClintHandler sender) throws IOException {
        for(ClintHandler clint:clients){
            if (clint==sender){
                continue;
            }
            clint.sendMessage(sender.getUsername()+": "+message);
        }

    }
    public synchronized void subscribe(ClintHandler handler){
        clients.add(handler);
    }
    public synchronized void unSubscribe(ClintHandler handler){
        clients.remove(handler);
    }
    public boolean isNicknameAlreadyBusy(String username){
        for (ClintHandler handler:clients){
            if (handler.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    public synchronized void sendMessageTo(ClintHandler sender, String userWriteName, String message) throws IOException {
        for (ClintHandler handler:clients){
            if (handler.getUsername().equals(userWriteName)){
               handler.sendMessage(sender.getUsername()+": "+message);
               return;
            }
        }
        sender.sendMessage("Такой пользователь не в сети или не существует");

    }
}
