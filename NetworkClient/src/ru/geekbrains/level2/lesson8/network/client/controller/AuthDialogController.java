package ru.geekbrains.level2.lesson8.network.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.geekbrains.level2.lesson8.network.client.Client;
import ru.geekbrains.level2.lesson8.network.client.model.Network;

public class AuthDialogController {
    private @FXML PasswordField passwordField;
    private @FXML Button authButton;
    private @FXML TextField loginField;
    private Network network;
    private Client client;

    @FXML
    public void executeAuth(ActionEvent actionEvent) {
        String login=loginField.getText();
        String password=passwordField.getText();
        if(login==null||login.isEmpty()||password==null||login.isEmpty()){
            Client.showNetworkError("User name and password shouldn't be empty","Auth err");
            return;
        }
        String authErr=network.sendAuthCommand(login,password);
        if(authErr==null){
            client.openChat();
        } else {
            Client.showNetworkError(authErr,"auth error");
        }
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
