package ru.geekbrains.level2.lesson8.network.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.geekbrains.level2.lesson8.network.client.NetworkChatClient;
import ru.geekbrains.level2.lesson8.network.client.model.Network;

public class ChangeNameDialogController {
    private @FXML
    TextField newNameField;
    private @FXML
    PasswordField passwordField;
    private Network network;
    private NetworkChatClient clientApp;

    @FXML
    public void changeName(ActionEvent actionEvent) {
        String newName = newNameField.getText();
        String password = passwordField.getText();
        if (newName == null || newName.isBlank() || password == null || password.isBlank()) {
            NetworkChatClient.showNetworkError("Username and password should be not empty!", "Name change error");
            return;
        }

        String username = network.getUsername();
        network.sendCommandChangeName(username, newName, password);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setClientApp(NetworkChatClient clientApp) {
        this.clientApp = clientApp;
    }
}
