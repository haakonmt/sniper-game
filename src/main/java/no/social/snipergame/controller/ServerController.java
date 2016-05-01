package no.social.snipergame.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import no.social.snipergame.model.Client;
import no.social.snipergame.model.Game;
import no.social.snipergame.util.Constants;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static no.social.snipergame.util.Constants.*;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 18.04.2016 11.19.
 */
public class ServerController implements Initializable {

    private static Long currentId = 1L;

    @FXML private ListView<Client> clientListView;
    @FXML private Label ipLabel;
    @FXML private Label portLabel;
    @FXML private TextArea statusArea;

    private final List<Client> clients = new ArrayList<>();

    private MqttClient server;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Auto scroll to bottom
        statusArea.textProperty().addListener((observable, oldValue, newValue) -> statusArea.setScrollTop(Double.MAX_VALUE));
        ipLabel.setText("m21.cloudmqtt.com");
        portLabel.setText("12338");

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(MQTT_USERNAME);
            options.setPassword(MQTT_PASSWORD);
            server = new MqttClient("tcp://" + SERVER_HOSTNAME + ":" + SERVER_PORT, "server");
            server.setCallback(new MqttServerHandler());
            server.connect(options);
            server.subscribe("server");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private Game startGameIfPossible() {
        for (int i = 0; i < clients.size(); i++) {
            for (int j = i+1; j < clients.size(); j++) {
                Client c1 = clients.get(i), c2 = clients.get(j);
                if (c1.getType() != c2.getType() && c1.getPreferredDifficulty() == c2.getPreferredDifficulty()) {
                    Client sniper = c1.getType() == Constants.PlayerType.SNIPER ? c1 : c2;
                    Client spotter = c1.getType() == Constants.PlayerType.SPOTTER ? c1 : c2;
                    clients.removeAll(Arrays.asList(c1, c2));
                    Platform.runLater(() -> clientListView.getItems().setAll(clients));
                    return new Game(currentId++, c1.getPreferredDifficulty(), sniper, spotter);
                }
            }
        }
        return null;
    }

    public void close() {
        try {
            server.disconnect();
            server.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private class MqttServerHandler extends MqttHandler {

        @Override
        public void internalMessageArrived(String topic, MqttMessage message) {
            statusArea.appendText("Message received on " + topic + ": " + message.toString() + "\n");
            clients.add(gson.fromJson(message.toString(), Client.class));
            Game newGame = startGameIfPossible();
            if (newGame != null) {
                Platform.runLater(() -> {
                    try {
                        server.publish("client/" + newGame.getSpotter().getNickName() + "/game", gson.toJson(newGame.toSpotter()).getBytes(), 2, false);
                        server.publish("client/" + newGame.getSniper().getNickName() + "/game", gson.toJson(newGame.toSniper()).getBytes(), 2, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
