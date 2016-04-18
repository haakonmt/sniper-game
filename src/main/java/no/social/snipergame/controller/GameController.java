package no.social.snipergame.controller;

import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.ImageCursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.effect.InnerShadow;
import no.social.snipergame.model.Coordinates;
import no.social.snipergame.model.Game;
import no.social.snipergame.network.Client;
import no.social.snipergame.network.NetworkConnection;
import no.social.snipergame.network.Server;
import no.social.snipergame.util.Constants;

import java.net.URL;
import java.util.ResourceBundle;

import static no.social.snipergame.util.Constants.SERVER_HOSTNAME;
import static no.social.snipergame.util.Constants.SERVER_PORT;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 30.03.2016 13.58.
 */
public class GameController implements Initializable  {

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

    private Game game;

    private Gson gson;

    private Image scope;

    private Coordinates markedCoordinates, curserCoordinates;

    private boolean sniper = true;

    private String name = sniper ? "Sniper" : "Spotter";
    private NetworkConnection connection = sniper ? createServer() : createClient();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scope = new Image("/crosshair.png");
        mark = new ImageView("/red-x.png");
        game = sniper ? new Game(Constants.Difficulty.EASY) : new Game(Constants.Difficulty.EASY);
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
            connection = createServer();
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
        try {
            connection.startConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return new Client(SERVER_HOSTNAME, SERVER_PORT, data -> Platform.runLater(() -> processData(data.toString())));
    }

    private Server createServer() {
        return new Server(SERVER_PORT, data -> Platform.runLater(() -> processData(data.toString())));
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
