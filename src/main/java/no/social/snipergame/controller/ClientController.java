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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import no.social.snipergame.model.Client;
import no.social.snipergame.model.Coordinates;
import no.social.snipergame.model.Game;
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

    private ImageView mark;

    private Game game = null;

    private Client client;

    private RunnableClient runnableClient;

    private Gson gson;

    private Image scope;

    private Coordinates markedCoordinates, curserCoordinates;

    private boolean sniper;

    private String name = sniper ? "Sniper" : "Spotter";

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
        this.sniper = game.isSniper();
        scope = new Image("/crosshair.png");
        mark = new ImageView("/red-x.png");

        // Select a random tile from the list
        // Game window: X:24, Y:17
        for (int i = 0; i < 23*16; i++) {
            StackPane stackPane = new StackPane(new ImageView("/icons/" + game.getTile() + ".png"));
            if (game.getPersons()[i] != null) stackPane.getChildren().add(game.getPersons()[i].compile());
            grid.getChildren().add(stackPane);
        }

        markedCoordinates = curserCoordinates =  new Coordinates(0,0);
        coordinateLabel.setText(markedCoordinates.toString());

        if (sniper) {
            windLabel.setText("");
            coordinateLabel.setText("");
            sendCoordinatesButton.setVisible(false);
            sendWindButton.setVisible(false);
            grid.setEffect(new ColorAdjust(0, -1, 0, 0));
        }
        else {
            windLabel.setText(game.getWind().toString());
        }

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
            System.out.println("Index: " + index);
            ((StackPane) grid.getChildren().get((y*23) + x)).getChildren().add(mark);
        });
        gameBox.setVisible(true);
    }

    @FXML
    private void sendMessage() throws Exception {

    }

    @FXML
    private void sendWind() throws Exception {
    }

    @FXML
    private void sendCoords() throws Exception {
    }

    @FXML
    private void connect() {
        runnableClient = new RunnableClient(ipField.getText(),
                Integer.parseInt(portField.getText()),
                new Client(nickNameField.getText(), typeBox.getSelectionModel().getSelectedItem(), difficultyBox.getSelectionModel().getSelectedItem()));

        new Thread(runnableClient).start();
        connectButton.setDisable(true);
    }

    private class RunnableClient implements Runnable{

        String dstAddress;
        int dstPort;
        Client client;

        RunnableClient(String addr, int port, Client client) {
            dstAddress = addr;
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

                while (true) {
                    if (client != null) {
                        dataOutputStream.writeUTF(gson.toJson(client));
                    }
                    Game object = gson.fromJson(dataInputStream.readUTF(), Game.class);
                    System.out.println("Game received");
                    game = object;
                    Platform.runLater(ClientController.this::initGame);
                }

            } catch (IOException ex) {
                Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
