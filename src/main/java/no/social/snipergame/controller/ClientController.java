package no.social.snipergame.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.ImageCursor;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import no.social.snipergame.model.*;
import no.social.snipergame.model.asset.*;
import no.social.snipergame.util.Constants;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static no.social.snipergame.util.Constants.*;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 30.03.2016 13.58.
 */
public class ClientController implements Initializable  {

    @FXML private Label reviewLabel;
    @FXML private Label waitingLabel;
    @FXML private VBox textBox;
    @FXML private Label gameOverLabel;
    @FXML private Button connectButton;
    @FXML private HBox gameBox;
    @FXML private ComboBox<Constants.Difficulty> difficultyBox;
    @FXML private TextField nickNameField;
    @FXML private ComboBox<Constants.PlayerType> typeBox;
    @FXML private Label statusLabel;

    @FXML private TilePane grid;
    @FXML private Label currentCoordinatesLabel;
    @FXML private Button sendWindButton;
    @FXML private Button sendCoordinatesButton;
    @FXML private Button fireButton;
    @FXML private Label timeLabel;
    @FXML private TextArea chatArea;
    @FXML private Label windLabel;
    @FXML private Label coordinateLabel;
    @FXML private TextField chatField;

    private Game game;
    private Client client, opponent;
    private Gson gson;
    private Coordinates markedCoordinates;

    private MqttClient mqttClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeBox.setItems(FXCollections.observableArrayList(PlayerType.values()));
        typeBox.getSelectionModel().select(0);
        difficultyBox.setItems(FXCollections.observableArrayList(Difficulty.values()));
        difficultyBox.getSelectionModel().select(0);
        gson = new GsonBuilder().create();
    }

    private void initGame() {
        waitingLabel.setVisible(false);
        opponent = game.isSniper() ? game.getSpotter() : game.getSniper();
        // Game window: X:23, Y:16
        grid.getChildren().clear();
        for (int i = 0; i < 23*16; i++) {
            StackPane stackPane = new StackPane(new ImageView("/icons/" + game.getTile() + ".png"));
            if (game.getPersons()[i] != null) stackPane.getChildren().add(game.getPersons()[i].compile());
            grid.getChildren().add(stackPane);
        }

        long milliStart = game.getStartMillis();
        Timeline timer = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            long millis = System.currentTimeMillis() - milliStart;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
            millis -= TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis));
            timeLabel.setText(String.format("%d:%02d:%03d", minutes, seconds, millis));
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();

        markedCoordinates =  new Coordinates(0,0);
        coordinateLabel.setText(markedCoordinates.toString());
        fireButton.setVisible(false);

        // Sniper specific stuff
        if (game.isSniper()) {
            windLabel.setText("");
            coordinateLabel.setText("");
            fireButton.setVisible(true);
            sendCoordinatesButton.setVisible(false);
            sendWindButton.setVisible(false);
            grid.setEffect(new ColorAdjust(0, -1, 0, 0));
            textBox.getChildren().add(0, generateInfoBox());
        }
        // Spotter specific stuff
        else {
            windLabel.setText(game.getWind().toString());
            fireButton.setVisible(false);
        }

        Image scope = new Image("/cross_hair.png");
        ImageView mark = new ImageView("/red-x.png");
        grid.setCursor(new ImageCursor(scope, scope.getWidth()/2, scope.getHeight()/2));
        grid.setOnMouseMoved(event -> currentCoordinatesLabel.setText("X: " + (int) event.getX()/40 + " Y: " + (int) event.getY()/40));
        grid.setOnMouseClicked(event -> {
            int x = (int) (event.getX()/40);
            int y = (int) (event.getY()/40);
            markedCoordinates.setX(x);
            markedCoordinates.setY(y);
            coordinateLabel.setText(markedCoordinates.toString());
            ((StackPane) grid.getChildren().get((y*23) + x)).getChildren().remove(mark);
            Dimension2D dim = ImageCursor.getBestSize(event.getScreenX(), event.getScreenY());
            mark.setFitHeight(dim.getHeight());
            mark.setFitWidth(dim.getWidth());
            mark.setLayoutX(x);
            mark.setLayoutY(y);
            ((StackPane) grid.getChildren().get((y*23) + x)).getChildren().add(mark);
        });
        gameOverLabel.setVisible(false);
        gameBox.setVisible(true);
    }

    private VBox generateInfoBox() {
        Person target = game.getTargetPerson();
        Hair hair = new Hair(target.getHair());
        Shirt shirt = new Shirt(target.getShirt());
        VBox infoBox = new VBox(
                new Label("Sex: " + target.getSex()),
                new Label("Hair color: " + hair.getColor()),
                new Label("Hair type: " + hair.getType()),
                new Label("Skin color: " + new Base(target.getBase()).getColor()),
                new Label("Shirt color: " + shirt.getColor()),
                new Label("Shirt type: " + shirt.getType()),
                new Label("Pants color: " + new Pants(target.getPants()).getColor()),
                new Label("Shoe color: " + new Shoes(target.getShoes()).getColor()));
        infoBox.setPadding(new Insets(10, 10, 10, 10));
        infoBox.setStyle("-fx-border-color: darkred");
        return infoBox;
    }

    @FXML
    private void sendMessage() throws Exception {
        Message message = new Message(game.getId(), client,
                (client.getType() == PlayerType.SNIPER ? game.getSpotter() : game.getSniper()), chatField.getText());
        Platform.runLater(()-> chatArea.appendText(message + "\n"));
        mqttClient.publish("client/" + opponent.getNickName() + "/message", new MqttMessage(gson.toJson(message).getBytes()));
    }

    @FXML
    private void sendWind() throws Exception {
        mqttClient.publish("client/" + opponent.getNickName() + "/wind", new MqttMessage(gson.toJson(game.getWind()).getBytes()));
    }

    @FXML
    private void sendCoordinates() throws Exception {
        mqttClient.publish("client/" + opponent.getNickName() + "/coordinates", new MqttMessage(gson.toJson(markedCoordinates).getBytes()));
    }

    @FXML
    private void connect() {
        client = new Client(nickNameField.getText(), typeBox.getSelectionModel().getSelectedItem(), difficultyBox.getSelectionModel().getSelectedItem());
        instantiateMqttClient();
        waitingLabel.setVisible(true);
        Timeline waitingAnimation = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            String wlText = waitingLabel.getText();
            if (wlText.endsWith("...")) wlText = wlText.substring(0, wlText.length()-3);
            else wlText += ".";
            waitingLabel.setText(wlText);
        }));
        waitingAnimation.setCycleCount(Animation.INDEFINITE);
        waitingAnimation.play();
        connectButton.setDisable(true);
    }

    private void instantiateMqttClient() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(MQTT_USERNAME);
            options.setPassword(MQTT_PASSWORD);
            mqttClient = new MqttClient("tcp://" + SERVER_HOSTNAME + ":" + SERVER_PORT, client.getNickName());
            mqttClient.setCallback(new MqttClientHandler());
            mqttClient.connect(options);
            mqttClient.subscribe("client/" + client.getNickName() + "/#");
            mqttClient.publish("server", new MqttMessage(gson.toJson(client).getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void fire() throws Exception {
        int firedX = markedCoordinates.getX(), firedY = markedCoordinates.getY();
        Wind wind = game.getWind();
        int winX = game.getWinX(), winY = game.getWinY();
        switch (wind.getDirection()) {
            case NORTH: winY += wind.getSpeed(); break;
            case SOUTH: winY -= wind.getSpeed(); break;
            case EAST: winX -= wind.getSpeed(); break;
            case WEST: winX += wind.getSpeed(); break;
            case NORTH_EAST:
                winX -= wind.getSpeed();
                winY += wind.getSpeed();
                break;
            case NORTH_WEST:
                winX += wind.getSpeed();
                winY += wind.getSpeed();
                break;
            case SOUTH_EAST:
                winX -= wind.getSpeed();
                winY -= wind.getSpeed();
                break;
            case SOUTH_WEST:
                winX += wind.getSpeed();
                winY -= wind.getSpeed();
                break;
        }
        boolean isWon = (firedX == winX && firedY == winY);
        handleGameFinished(isWon);
        mqttClient.publish("client/" + opponent.getNickName() + "/game_over", new MqttMessage(String.valueOf(isWon).getBytes()));
    }

    private void handleGameFinished(boolean isWon) {
        game.setWon(isWon);
        gameBox.setVisible(false);
        gameOverLabel.setVisible(true);
        gameOverLabel.setText((game.isWon() ? "You won!" : "You lost..") + "\n" + timeLabel.getText());
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(5000);
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> {
            gameOverLabel.setVisible(false);
            reviewLabel.setVisible(true);
            reviewLabel.setText("Take a moment to reflect on your performance and answer these questions for yourself." +
                    "\n1. Did you win? If yes, why? If no, why not?"
                    + "\n2. Were there issues due to miscommunications?"
                    + "\n3. Could you have been done faster?"
                    + "\n4. If you played it a second time around, do you think you could have done better?"
                    + "\n5. What part of your communication could be improved?");
        });
        new Thread(sleeper).start();
    }

    public void close() {
        try {
            mqttClient.disconnect();
            mqttClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private class MqttClientHandler extends MqttHandler {

        @Override
        public void internalMessageArrived(String topic, MqttMessage message) {
            Platform.runLater(() -> {
                if (topic.endsWith("game")) {
                    game = gson.fromJson(message.toString(), Game.class);
                    initGame();
                } else if (topic.endsWith("message")) {
                    chatArea.appendText(gson.fromJson(message.toString(), Message.class).toString() + "\n");
                } else if (topic.endsWith("coordinates")) {
                    chatArea.appendText("Coordinates received.\n");
                    coordinateLabel.setText(gson.fromJson(message.toString(), Coordinates.class).toString());
                } else if (topic.endsWith("wind")) {
                    chatArea.appendText("Wind sent.\n");
                    windLabel.setText(gson.fromJson(message.toString(), Wind.class).toString());
                } else if (topic.endsWith("game_over")) {
                    handleGameFinished(Boolean.valueOf(message.toString()));
                }
            });
        }
    }
}
