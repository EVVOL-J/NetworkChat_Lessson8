package ru.geekbrains.level2.lesson8.network.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.geekbrains.level2.lesson8.network.client.controller.AuthDialogController;
import ru.geekbrains.level2.lesson8.network.client.controller.ChangeNameDialogController;
import ru.geekbrains.level2.lesson8.network.client.controller.ViewController;
import ru.geekbrains.level2.lesson8.network.client.history.LogHistory;
import ru.geekbrains.level2.lesson8.network.client.model.Network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


public class NetworkChatClient extends Application {

    public static final List<String> USERS_TEST_DATA = List.of("Oleg", "Alexey", "Peter");
    private Stage changeNameDialogStage;
    private Stage primaryStage;
    private Stage authDialogStage;
    private Network network;
    private ViewController viewController;
    private AuthDialogController authController ;
    private LogHistory logHistory;


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        network = new Network();
        if (!network.connect()) {
            showNetworkError("", "Failed to connect to server");
            return;
        }

        openAuthDialog(primaryStage);
        createChatDialog(primaryStage);
    }

    private void createChatDialog(Stage primaryStage) throws java.io.IOException {
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(NetworkChatClient.class.getResource("view/view.fxml"));

        Parent root = mainLoader.load();

        primaryStage.setTitle("Messenger");
        primaryStage.setScene(new Scene(root, 600, 400));

        viewController = mainLoader.getController();
        viewController.setNetwork(network);
        viewController.setClientApp(this);

        primaryStage.setOnCloseRequest(event -> {network.close();

                logHistory.closeFile();

        });
    }

    private void openAuthDialog(Stage primaryStage) throws java.io.IOException {
        FXMLLoader authLoader = new FXMLLoader();

        authLoader.setLocation(NetworkChatClient.class.getResource("view/authDialog.fxml"));
        Parent authDialogPanel = authLoader.load();
        authDialogStage = new Stage();

        authDialogStage.setTitle("Аутентификая чата");
        authDialogStage.initModality(Modality.WINDOW_MODAL);
        authDialogStage.initOwner(primaryStage);
        Scene scene = new Scene(authDialogPanel);
        authDialogStage.setScene(scene);
        authDialogStage.show();



        authController = authLoader.getController();
        authController.setNetwork(network);
        authController.setClientApp(this);
    }

    public static void showNetworkError(String errorDetails, String errorTitle) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Network Error");
        alert.setHeaderText(errorTitle);
        alert.setContentText(errorDetails);
        alert.showAndWait();
    }

    public static void showNetworkINFO(String errorDetails, String errorTitle) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Network INFORMATION");
        alert.setHeaderText(errorTitle);
        alert.setContentText(errorDetails);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void openChat() {
        authDialogStage.close();

            logHistory=new LogHistory("history_"+authController.getLogin()+".txt");
            viewController.appendMessageWithoutData(logHistory.readLastNLines(10));

        primaryStage.show();
        primaryStage.setTitle(network.getUsername());
        network.waitMessages(viewController);
    }

    public void openChangeNameDialogController() throws IOException {
        FXMLLoader changeNameLoader = new FXMLLoader();
        changeNameLoader.setLocation(NetworkChatClient.class.getResource("view/changeNameDialog.fxml"));
        Parent changeNameDialogPanel = changeNameLoader.load();
        changeNameDialogStage = new Stage();

        changeNameDialogStage.setTitle("Изменение имени");
        changeNameDialogStage.initModality(Modality.WINDOW_MODAL);
        changeNameDialogStage.initOwner(primaryStage);
        Scene scene = new Scene(changeNameDialogPanel);
        changeNameDialogStage.setScene(scene);
        changeNameDialogStage.show();

        ChangeNameDialogController changeNameDialogController = changeNameLoader.getController();
        changeNameDialogController.setNetwork(network);
        changeNameDialogController.setClientApp(this);


    }

    public LogHistory getLogHistory() {
        return logHistory;
    }
}
