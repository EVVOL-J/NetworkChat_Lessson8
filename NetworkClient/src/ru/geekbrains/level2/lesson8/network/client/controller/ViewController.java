package ru.geekbrains.level2.lesson8.network.client.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ru.geekbrains.level2.lesson8.network.client.NetworkChatClient;
import ru.geekbrains.level2.lesson8.network.client.model.Network;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

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
    private NetworkChatClient chatClient;
    private String selectedRecipient;


    @FXML
    public void initialize() {
        usersList.setItems(FXCollections.observableArrayList(NetworkChatClient.USERS_TEST_DATA));
        sendButton.setOnAction(event -> sendMessage());
        textField.setOnAction(event -> sendMessage());

//        usersList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//                selectedRecipient = newValue;
//        });

        usersList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = usersList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                usersList.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell;
        });
    }

    private void sendMessage() {
        String message = textField.getText();
        appendMessage("Я: " + message);
        textField.clear();

        try {
            if (selectedRecipient != null) {
                network.sendPrivateMessage(message, selectedRecipient);
            } else {
                network.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            String errorMessage = "Failed to send message";
            NetworkChatClient.showNetworkError(e.getMessage(), errorMessage);
        }
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setClientApp(NetworkChatClient networkChatClient) {
        this.chatClient = networkChatClient;
    }

    public void appendMessage(String message) {
        String timestamp = DateFormat.getInstance().format(new Date());
        String sendMessage=timestamp+"\n"+message+"\n\n";
        chatHistory.appendText(sendMessage);
        chatClient.getLogHistory().writeFile(sendMessage);
    }
    public void appendMessageWithoutData(String message) {
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(System.lineSeparator());

    }

    public void showError(String title, String message) {
        NetworkChatClient.showNetworkError(message, title);
    }

    public void showInfo(String message, String title) {
        NetworkChatClient.showNetworkINFO(message, title);

    }

    public void updateUsersList(List<String> users) {
        usersList.setItems(FXCollections.observableArrayList(users));
    }

    public void settingsUsername(ActionEvent actionEvent) throws IOException {
        chatClient.openChangeNameDialogController();
//        try {
//            network.sendCommandChangeName("Oleg","lol","pass1");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
