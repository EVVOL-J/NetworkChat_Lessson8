package ru.geekbrains.level2.lesson8.network.server.chat;



import ru.geekbrains.level2.lesson8.network.clientserver.Command;
import ru.geekbrains.level2.lesson8.network.server.ServerApp;
import ru.geekbrains.level2.lesson8.network.server.chat.auth.AuthService;
import ru.geekbrains.level2.lesson8.network.server.chat.auth.DataBaseAuthService;
import ru.geekbrains.level2.lesson8.network.server.chat.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;


public class MyServer {


    private final ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final DataBaseAuthService authService;
    private ExecutorService executorService;
    private static final Logger log= Logger.getLogger(MyServer.class.getName());
    private Handler h=new FileHandler("log.txt");

    public MyServer(int port) throws IOException {

        this.serverSocket = new ServerSocket(port);
        this.authService = new DataBaseAuthService();
        h.setFormatter(new SimpleFormatter());
        log.addHandler(h);

    }

    public void start() throws IOException {

        authService.start();
        executorService= Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
        while (true) {
            waitAndProcessNewClientConnection();
        }
    } catch (IOException e) {

    } finally {
        authService.stop();
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    log.log(Level.SEVERE,"Error close server Socket");
                }
            }
        });

        executorService.shutdown();



    }

    private void waitAndProcessNewClientConnection() throws IOException {
        log.log(Level.INFO,"Ожидание нового подключения....");
        Socket clientSocket = serverSocket.accept();
        log.log(Level.INFO,"Клиент подключился");
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
        List<String> usersNames = getAllUsersNames();
        broadcastMessage(null, Command.updateUserListCommand(usersNames));
    }

    private List<String> getAllUsersNames() {
        List<String> usernames = new ArrayList<>();
        for (ClientHandler client : clients) {
            usernames.add(client.getUsername());
        }
        return usernames;
    }

    public synchronized void unsubscribe(ClientHandler handler) throws IOException {
        clients.remove(handler);
        List<String> usersNames = getAllUsersNames();
        broadcastMessage(null, Command.updateUserListCommand(usersNames));
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

    public boolean updateUserList(String newUserName, String password, ClientHandler clientHandler) throws IOException {
        if (authService.getDataBaseController().changeUserNameByPassword(clientHandler.getUsername(), newUserName, password)) {
            clientHandler.setUsername(newUserName);
            List<String> usersNames = getAllUsersNames();
            sendPrivateMessage(newUserName, Command.changeUserNameOKCommand(newUserName));
            broadcastMessage(null, Command.updateUserListCommand(usersNames));
            return true;
        } else return false;
    }

    public static Logger getLOGGER() {
        return log;
    }
}