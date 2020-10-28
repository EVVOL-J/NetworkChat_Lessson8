package ru.geekbrains.level2.lesson8.network.server.chat.handler;

import ru.geekbrains.level2.lesson8.network.server.chat.MyServer;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClintHandler {
    public static final String AUTH_CMD_PREFIX = "/auth";
    private final String AUTHOK_CMD_PREFIX="/authok";
    private final String AUTHERR_CMD_PREFIX="/autherr";

    private MyServer myServer;
    private Socket clintSocket;

    private DataInputStream in;
    private DataOutputStream out;

    private String username;

    public ClintHandler(MyServer myServer, Socket clintSocket) {
        this.myServer=myServer;
        this.clintSocket=clintSocket;
    }
    public void handle() throws IOException {
        in = new DataInputStream(clintSocket.getInputStream());
        out = new DataOutputStream(clintSocket.getOutputStream());
        new Thread(()->{
            try {
                authentication();
                readMassages();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    closeConnection();
                } catch (IOException e) {
                    System.err.println("Failed to close connection");
                }
            }
        }).start();
    }

    private void readMassages() throws IOException {
        while (true){
            String message=in.readUTF();
            System.out.println("Message from: "+username+" "+ message);
            if(message.startsWith("/end")){
                break;
            }
            else if(message.startsWith("/w")){
                String[] parseMessage=message.split("\\s+",3);
                    if(parseMessage.length==3){
                    myServer.sendMessageTo(this,parseMessage[1],parseMessage[2]);}
            }
            else
            {myServer.broadcastMessage(message,this);}
        }

    }

    private void authentication() throws IOException {
        while (true){String message=in.readUTF();
        if(message.startsWith(AUTH_CMD_PREFIX)) {
            String[] parts = message.split("\\s+", 3);
            String login = parts[1];
            String password = parts[2];
            this.username = myServer.getAuthService().getUserNameByLoginAndPassword(login, password);
            if (username!=null ) {
                if (myServer.isNicknameAlreadyBusy(username)){
                  out.writeUTF(AUTHERR_CMD_PREFIX+" Login and/or password are already used! \n");
                  continue;
                }
                else {
                out.writeUTF(AUTHOK_CMD_PREFIX+" "+ username);
                myServer.broadcastMessage(" Join to chat",this);
                myServer.subscribe(this);
                break;}
            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX+" Login and/or password are incorrect! Please, try again...\n");
            }
        }else out.writeUTF(AUTHERR_CMD_PREFIX+" /auth command is required\n");
        }

    }

    private void closeConnection() throws IOException {
        myServer.unSubscribe(this);
        clintSocket.close();
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message+"\n");
    }
}
