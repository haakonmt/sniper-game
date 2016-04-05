package no.social.snipergame.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

import static no.social.snipergame.Constants.IP;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 30.03.2016 14.00.
 */
public class ServerController implements Initializable {

    @FXML private Button openButton;
    @FXML private Button closeButton;
    @FXML private ListView gameListView;
    @FXML private ListView clientListView;
    @FXML private Label statusLabel;
    @FXML private Label ipLabel;
    @FXML private TextArea statusArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ipLabel.setText(IP);
    }

    @FXML
    private void openServer() {
        openButton.setVisible(false);
        closeButton.setVisible(true);
    }

    @FXML
    private void closeServer() {
        closeButton.setVisible(false);
        openButton.setVisible(true);
    }
}
