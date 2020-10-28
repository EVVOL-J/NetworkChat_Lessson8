package ru.geekbrains.level2.lesson8.network.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.geekbrains.level2.lesson8.network.client.controller.AuthDialogController;
import ru.geekbrains.level2.lesson8.network.client.controller.ViewController;
import ru.geekbrains.level2.lesson8.network.client.model.Network;

import java.util.List;


public class Client extends Application {

    public static final List<String> USERS_TEST_DATA = List.of("Oleg", "Alexey", "Peter");
    private Stage authDialogStage;
    private Stage primaryStage;
    private Network network;
    private ViewController viewController;


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage=primaryStage;
        FXMLLoader authLoader = new FXMLLoader();

        authLoader.setLocation(Client.class.getResource("view/authDialog.fxml"));
        Parent authDialogPanel = authLoader.load();
        authDialogStage = new Stage();

        authDialogStage.setTitle("Аутентификая чата");
        authDialogStage.initModality(Modality.WINDOW_MODAL);
        this.primaryStage = primaryStage;
        authDialogStage.initOwner(this.primaryStage);
        Scene scene = new Scene(authDialogPanel);
        authDialogStage.setScene(scene);
        authDialogStage.show();

        network = new Network();
        if (!network.connect()) {
            showNetworkError("", "Failed to connect to server");
        }
        AuthDialogController controller= authLoader.getController();
        controller.setNetwork(network);
        controller.setClient(this);


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("view/view.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Messenger");
        primaryStage.setScene(new Scene(root, 600, 400));

//        primaryStage.show();


        viewController = loader.getController();
        viewController.setNetwork(network);

//        network.waitMessages(viewController);

        primaryStage.setOnCloseRequest(event -> {
            network.close();
        });
    }

    public static void showNetworkError(String errorDetails, String errorTitle) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Network Error");
        alert.setHeaderText(errorTitle);
        alert.setContentText(errorDetails);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void openChat() {
        authDialogStage.close();
        primaryStage.setTitle(network.getUserName());
        primaryStage.show();
        network.waitMessages(viewController);

    }
}
