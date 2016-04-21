package no.social.snipergame.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.ImageCursor;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import no.social.snipergame.model.*;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 30.03.2016 13.58.
 */
public class ClientController implements Initializable  {

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
    private Coordinates markedCoordinates, cursorCoordinates;
    private boolean sniper;

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
        connection = sniper ? createSniperConnection() : createSpotterConnection();
        try {
            connection.startConnection();
        } catch (Exception ignore) {
        }
        // Game window: X:23, Y:16
        for (int i = 0; i < 23*16; i++) {
            StackPane stackPane = new StackPane(new ImageView("/icons/" + game.getTile() + ".png"));
            if (game.getPersons()[i] != null) stackPane.getChildren().add(game.getPersons()[i].compile());
            grid.getChildren().add(stackPane);
        }

        markedCoordinates = cursorCoordinates =  new Coordinates(0,0);
        coordinateLabel.setText(markedCoordinates.toString());

        // Sniper specific stuff
        if (sniper) {
            windLabel.setText("");
            coordinateLabel.setText("");
            sendCoordinatesButton.setVisible(false);
            sendWindButton.setVisible(false);
            grid.setEffect(new ColorAdjust(0, -1, 0, 0));
        }
        // Spotter specific stuff
        else {
            windLabel.setText(game.getWind().toString());
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
            int index = y*x;
            index += x;
            ((StackPane) grid.getChildren().get((y*23) + x)).getChildren().add(mark);
        });
        gameBox.setVisible(true);
    }

    @FXML
    private void sendMessage() throws Exception {
        Client sender, receiver;
        if (client.getType() == Constants.PlayerType.SNIPER) {
            sender = game.getSniper();
            receiver = game.getSpotter();
        } else {
            sender = game.getSpotter();
            receiver = game.getSniper();
        }
        Message message = new Message(game.getId(), sender, receiver, chatField.getText());
        game.addMessage(message);
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

    private SpotterConnection createSpotterConnection() {
        return new SpotterConnection(Constants.SERVER_HOSTNAME, Constants.SERVER_PORT, data -> Platform.runLater(() -> processData(data.toString())));
    }

    private SniperConnection createSniperConnection() {
        return new SniperConnection(Constants.SERVER_PORT, data -> Platform.runLater(() -> processData(data.toString())));
    }

    private void processData(String json) {
        if (json.startsWith("WIND")) windLabel.setText(gson.fromJson(json.substring(4), Wind.class).toString());
        else if (json.startsWith("MESSAGE")) chatArea.appendText(gson.fromJson(json.substring(7), Message.class).toString() + "\n");
        else if (json.startsWith("COORDINATES")) coordinateLabel.setText(gson.fromJson(json.substring(11), Coordinates.class).toString());
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
                }

            } catch (IOException ex) {
                Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
