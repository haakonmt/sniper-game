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

    @FXML private HBox gameBox;
    @FXML private ComboBox<Constants.Difficulty> difficultyBox;
    @FXML private TextField nickNameField;
    @FXML private ComboBox<Constants.PlayerType> typeBox;
    @FXML private TextField ipField;
    @FXML private TextField portField;
    @FXML private Label statusLabel;

    @FXML private Pane background;
    @FXML private TilePane grid;
    @FXML private Label currentCoordinatesLabel;
    @FXML private Button sendWindButton;
    @FXML private Button sendCoordinatesButton;
    @FXML private Label nameLabel;
    @FXML private Label timeLabel;
    @FXML private TextArea chatArea;
    @FXML private Label windLabel;
    @FXML private Label coordinateLabel;
    @FXML private TextField chatField;

    private ImageView mark;

    private Game game = null;

    private Client client;

    private Gson gson;

    private Image scope;

    private Coordinates markedCoordinates, curserCoordinates;

    private boolean sniper = true;

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
        scope = new Image("/crosshair.png");
        mark = new ImageView("/red-x.png");

        // Select a random tile from the list
        String[] tiles = new String[]{"brick", "desert", "grass", "ground", "ground2"};
        String tile = tiles[(int) (Math.random() * 5)];
        // Game window: X:24, Y:17
        for (int i = 0; i < 24*17; i++) {
            StackPane stackPane = new StackPane(new ImageView("/icons/" + tile + ".png"));
            if (game.getPersons()[i] != null) stackPane.getChildren().add(game.getPersons()[i]);
            grid.getChildren().add(stackPane);
        }

        markedCoordinates = curserCoordinates =  new Coordinates(0,0);
        nameLabel.setText(name);
        nameLabel.setTextFill(sniper ? Color.RED : Color.GREEN);
        coordinateLabel.setText(markedCoordinates.toString());

        if (sniper) {
            windLabel.setText("");
            coordinateLabel.setText("");
            sendCoordinatesButton.setVisible(false);
            sendWindButton.setVisible(false);
            background.setEffect(new ColorAdjust(0, -1, 0, 0));
        }

        background.setCursor(new ImageCursor(scope, scope.getWidth()/2, scope.getHeight()/2));

        grid.setOnMouseMoved(event -> currentCoordinatesLabel.setText("X: " + (int) event.getX()/10 + " Y: " + (int) event.getY()/10));

        //grid.setOnMouseExited(event -> coordinates.setText(OUTSIDE_TEXT));

        grid.setOnMouseClicked(event -> {
            //targetLabel.setText("Target locked at:\n" + "X: " + (int) event.getX()/10 + ", Y: " + (int) event.getY()/10);
            background.getChildren().remove(mark);
            Dimension2D dim = ImageCursor.getBestSize(event.getScreenX(), event.getScreenY());
            mark.setFitHeight(dim.getHeight());
            mark.setFitWidth(dim.getWidth());
            double X = event.getX() - mark.getFitWidth() / 2;
            double Y = event.getSceneY() - (1.65* mark.getFitHeight());
            mark.setLayoutX((int)X + 20);
            mark.setLayoutY((int)Y + 20);
            background.getChildren().add(mark);
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
        RunnableClient runnableClient
                = new RunnableClient(ipField.getText(),
                Integer.parseInt(portField.getText()),
                new no.social.snipergame.model.Client(nickNameField.getText(), typeBox.getSelectionModel().getSelectedItem(), difficultyBox.getSelectionModel().getSelectedItem()));

        new Thread(runnableClient).start();
    }

    private class RunnableClient implements Runnable{

        String dstAddress;
        int dstPort;
        String response = "";
        Client client;

        RunnableClient(String addr, int port, no.social.snipergame.model.Client client) {
            dstAddress = addr;
            dstPort = port;
            this.client = client;
        }

        @Override
        public void run() {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if(client != null){
                    dataOutputStream.writeUTF(gson.toJson(client));
                }

                response = dataInputStream.readUTF();

                Object object = gson.fromJson(response, Object.class);
                if (object instanceof Game) {
                    game = (Game) object;
                    initGame();
                }

            } catch (IOException ex) {
                Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {

                Platform.runLater(() -> statusLabel.setText(response));

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }

    }
}
