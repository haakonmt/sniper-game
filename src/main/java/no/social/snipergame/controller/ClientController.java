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
import no.social.snipergame.network.NetworkConnection;
import no.social.snipergame.network.SniperConnection;
import no.social.snipergame.network.SpotterConnection;
import no.social.snipergame.util.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 30.03.2016 13.58.
 */
public class ClientController implements Initializable  {

    @FXML private VBox textBox;
    @FXML private Button fireButton;
    @FXML private Label gameOverLabel;
    @FXML private Button connectButton;
    @FXML private HBox gameBox;
    @FXML private ComboBox<Constants.Difficulty> difficultyBox;
    @FXML private TextField nickNameField;
    @FXML private ComboBox<Constants.PlayerType> typeBox;
    @FXML private TextField ipField;
    @FXML private TextField portField;
    @FXML private Label statusLabel;

    @FXML private TilePane grid;
    @FXML private Label currentCoordinatesLabel;
    @FXML private Button sendWindButton;
    @FXML private Button sendCoordinatesButton;
    @FXML private Label timeLabel;
    @FXML private TextArea chatArea;
    @FXML private Label windLabel;
    @FXML private Label coordinateLabel;
    @FXML private TextField chatField;

    private Game game;
    private Client client;
    private Gson gson;
    private Coordinates markedCoordinates;
    private boolean sniper;

    private Timeline timer;

    // Connection between two clients
    private NetworkConnection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeBox.setItems(FXCollections.observableArrayList(Constants.PlayerType.values()));
        typeBox.getSelectionModel().select(0);

        difficultyBox.setItems(FXCollections.observableArrayList(Constants.Difficulty.values()));
        difficultyBox.getSelectionModel().select(0);

        ipField.setText("localhost");
        portField.setText("8080");
        gson = new GsonBuilder().create();
    }

    private void initGame() {
        sniper = game.isSniper();
        if (connection == null) {
            connection = sniper ? createSniperConnection() : createSpotterConnection();
            try {
                connection.startConnection();
            } catch (Exception ignore) {
            }
        }
        // Game window: X:23, Y:16
        grid.getChildren().clear();
        for (int i = 0; i < 23*16; i++) {
            StackPane stackPane = new StackPane(new ImageView("/icons/" + game.getTile() + ".png"));
            if (game.getPersons()[i] != null) stackPane.getChildren().add(game.getPersons()[i].compile());
            grid.getChildren().add(stackPane);
        }

        long milliStart = game.getStartMillis();
        timer = new Timeline(new KeyFrame(Duration.millis(1), event -> {
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

        // Sniper specific stuff
        if (sniper) {
            windLabel.setText("");
            coordinateLabel.setText("");
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

        Image scope = new Image("/crosshair.png");
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
                (client.getType() == Constants.PlayerType.SNIPER ? game.getSpotter() : game.getSniper()), chatField.getText());
        chatArea.appendText(message + "\n");
        connection.send("MESSAGE" + gson.toJson(message));
    }

    @FXML
    private void sendWind() throws Exception {
        connection.send("WIND" + gson.toJson(game.getWind()));
    }

    @FXML
    private void sendCoordinates() throws Exception {
        connection.send("COORDINATES" + gson.toJson(markedCoordinates));
    }

    @FXML
    private void connect() {
        client = new Client(nickNameField.getText(), typeBox.getSelectionModel().getSelectedItem(), difficultyBox.getSelectionModel().getSelectedItem());

        RunnableClient runnableClient = new RunnableClient(ipField.getText(),
                Integer.parseInt(portField.getText()), client);

        new Thread(runnableClient).start();
        connectButton.setDisable(true);
    }

    @FXML
    private void fire() throws Exception {
        boolean isWon = markedCoordinates.getX() == game.getWinX() && markedCoordinates.getY() == game.getWinY();
        handleGameFinished(isWon);
        connection.send("GAME_FINISHED" + String.valueOf(isWon));
    }

    private SpotterConnection createSpotterConnection() {
        return new SpotterConnection(Constants.SERVER_HOSTNAME, Constants.SERVER_PORT, data -> Platform.runLater(() -> processData(data.toString())));
    }

    private SniperConnection createSniperConnection() {
        return new SniperConnection(Constants.SERVER_PORT, data -> Platform.runLater(() -> processData(data.toString())));
    }

    private void processData(String json) {
        if (json.startsWith("WIND")) {
            chatArea.appendText("Wind sent.");
            windLabel.setText(gson.fromJson(json.substring(4), Wind.class).toString());
        }
        else if (json.startsWith("MESSAGE")) chatArea.appendText(gson.fromJson(json.substring(7), Message.class).toString() + "\n");
        else if (json.startsWith("COORDINATES")) {
            chatArea.appendText("Coordinates sent.\n");
            coordinateLabel.setText(gson.fromJson(json.substring(11), Coordinates.class).toString());
        }
        else if (json.startsWith("GAME_FINISHED")) handleGameFinished(Boolean.valueOf(json.substring(13)));
    }

    private void handleGameFinished(boolean isWon) {
        game.setGameOver(true);
        game.setWon(isWon);
        gameBox.setVisible(false);
        gameOverLabel.setVisible(true);
        gameOverLabel.setText(game.isWon() ? "You won!" : "You lost..");
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(5000);
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> {
            gameOverLabel.setVisible(false);
            //TODO: Show questionnaire here
        });
        new Thread(sleeper).start();
    }

    private class RunnableClient implements Runnable{

        final String dstAddress;
        final int dstPort;
        final Client client;

        RunnableClient(String dstAddress, int port, Client client) {
            this.dstAddress = dstAddress;
            dstPort = port;
            this.client = client;
        }

        @Override
        public void run() {
            Socket socket;
            DataOutputStream dataOutputStream;
            DataInputStream dataInputStream;

            try {
                socket = new Socket(dstAddress, dstPort);
                socket.setKeepAlive(true);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if (client != null) {
                    dataOutputStream.writeUTF(gson.toJson(client));
                }
                if (game == null) {
                    game = gson.fromJson(dataInputStream.readUTF(), Game.class);
                    Platform.runLater(ClientController.this::initGame);
                    Platform.runLater(() -> chatArea.appendText(game.getSniper().getNickName() + " (sniper) and "
                            + game.getSpotter().getNickName() + " (spotter) are now playing together.\n"));
                }

            } catch (IOException ex) {
                Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
