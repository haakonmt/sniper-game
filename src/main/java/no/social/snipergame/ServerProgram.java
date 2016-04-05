package no.social.snipergame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 10.49.
 */
public class ServerProgram extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/serverView.fxml"));
        primaryStage.setTitle("Sniper Recon - SERVER");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
