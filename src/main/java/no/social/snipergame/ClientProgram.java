package no.social.snipergame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.management.ManagementFactory;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 10.52.
 */
public class ClientProgram extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/clientView.fxml"));
        primaryStage.setTitle("Client - " + ManagementFactory.getRuntimeMXBean().getName());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
