package no.social.snipergame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import no.social.snipergame.controller.ClientController;

import java.lang.management.ManagementFactory;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 10.52.
 */
public class ClientProgram extends Application {

    private ClientController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream("/fxml/clientView.fxml"));
        controller = loader.getController();
        primaryStage.setTitle("Client - " + ManagementFactory.getRuntimeMXBean().getName());
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
