package no.social.snipergame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerProgram extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/serverView.fxml"));
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
