package no.social.snipergame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import no.social.snipergame.controller.ServerController;

import java.io.IOException;

public class ServerProgram extends Application {

    private ServerController controller;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream("/fxml/serverView.fxml"));
        controller = loader.getController();
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
