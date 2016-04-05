package no.social.snipergame.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import no.social.snipergame.model.Game;
import no.social.snipergame.network.Client;
import no.social.snipergame.network.NetworkConnection;
import no.social.snipergame.network.Server;
import no.social.snipergame.util.Constants;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 30.03.2016 13.58.
 */
public class GameController implements Initializable  {

    @FXML private Button sendWindButton;
    @FXML private Button sendCoordinatesButton;
    @FXML private Label nameLabel;
    @FXML private Label timeLabel;
    @FXML private TextArea chatArea;
    @FXML private Label windLabel;
    @FXML private Label coordinateLabel;
    @FXML private TextField chatField;

    private Game game;

    private boolean sniper;

    private String name = sniper ? "Sniper" : "Spotter";
    private NetworkConnection connection = sniper ? createServer() : createClient();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameLabel.setText(name);
        nameLabel.setTextFill(sniper ? Color.RED : Color.GREEN);
        if (sniper) {
            windLabel.setText("");
            coordinateLabel.setText("");
            sendCoordinatesButton.setVisible(false);
            sendWindButton.setVisible(false);
        }
        try {
            connection.startConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fireTimeChanged(String time) {
        timeLabel.setText(time);
    }

    @FXML
    private void sendMessage() throws Exception {
        //game.sendMessage(this, chatField.getText());
        String message = name + ": " + chatField.getText() + "\n";
        chatArea.appendText(message);
        connection.send(message);
        chatField.clear();

    }

    private Client createClient() {
        return new Client(Constants.SERVER_HOSTNAME, Constants.SERVER_PORT, data -> Platform.runLater(() -> processData(data.toString())));
    }

    private Server createServer() {
        return new Server(Constants.SERVER_PORT, data -> Platform.runLater(() -> processData(data.toString())));
    }

    private void processData(String s) {
        if (s.startsWith("WIND")) {
            windLabel.setText(s.substring(5));
        } else if (s.startsWith("COORDINATES")) {
            coordinateLabel.setText(s.substring(12));
        } else {
            chatArea.appendText(s);
        }
    }

    @FXML
    private void sendWind() throws Exception {
        connection.send("WIND " + windLabel.getText());
    }

    @FXML
    private void sendCoords() throws Exception {
        connection.send("COORDINATES " + coordinateLabel.getText());
    }

    public void setSniper(boolean sniper) {
        this.sniper = sniper;
    }
}
