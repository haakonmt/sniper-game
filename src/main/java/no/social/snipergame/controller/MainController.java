package no.social.snipergame.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static no.social.snipergame.Constants.PlayerType;
import static no.social.snipergame.Constants.PlayerType.SNIPER;
import static no.social.snipergame.Constants.PlayerType.SPOTTER;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 19.02.2016 10.49.
 */
public class MainController implements Initializable {

    @FXML private VBox mainBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void chooseSpotter() throws IOException {
        choosePlayer(SPOTTER);
    }

    @FXML
    public void chooseSniper() throws IOException {
        choosePlayer(SNIPER);
    }

    private void choosePlayer(PlayerType type) throws IOException {
        String resourceUrl = "/fxml/";
        switch (type) {
            case SNIPER: resourceUrl += "sniper"; break;
            case SPOTTER: resourceUrl += "spotter"; break;
        }

        Parent root = FXMLLoader.load(getClass().getResource(resourceUrl + "View.fxml"));
        Stage stage = (Stage) mainBox.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }
}
