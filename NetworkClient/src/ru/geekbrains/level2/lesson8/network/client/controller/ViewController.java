package ru.geekbrains.level2.lesson8.network.client.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import ru.geekbrains.level2.lesson8.network.client.Client;
import ru.geekbrains.level2.lesson8.network.client.model.Network;

import java.io.IOException;

public class ViewController {

    @FXML
    public ListView<String> usersList;

    @FXML
    private Button sendButton;
    @FXML
    private TextArea chatHistory;
    @FXML
    private TextField textField;
    private Network network;
    private String selectedUsername=null;


    @FXML
    public void initialize() {
        usersList.setItems(FXCollections.observableArrayList(Client.USERS_TEST_DATA));
        sendButton.setOnAction(event -> sendMessage());
        textField.setOnAction(event -> sendMessage());

        usersList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = usersList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                usersList.requestFocus();
                if (! cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedUsername = null;
                    } else {
                        selectionModel.select(index);
                        selectedUsername = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell ;
        });

    }

    private void sendMessage() {
        String message = textField.getText();
        appendMessage("Я: " + message);
        textField.clear();

        try {
            if (selectedUsername==null)
            {network.getOutputStream().writeUTF(message);}
            else network.getOutputStream().writeUTF("/w "+selectedUsername+" "+message);
        } catch (IOException e) {
            e.printStackTrace();
            String errorMessage = "Failed to send message";
            Client.showNetworkError(e.getMessage(), errorMessage);
        }
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void appendMessage(String message) {
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
    }


    public String getSelectedUsername() {
        return selectedUsername;
    }
}
