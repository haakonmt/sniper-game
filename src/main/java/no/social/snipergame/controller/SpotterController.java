package no.social.snipergame.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.scene.ImageCursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import no.social.snipergame.model.Wind;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 19.02.2016 10.35.
 */
public class SpotterController extends ClientController {

    @FXML private Label time, wind, coordinates, targetLabel;
    @FXML private TextArea chat;
    @FXML private TextField chatField;
    @FXML private Pane background;

    private Image scope;
    private ImageView mark;
    private long startTime;

    @FXML
    public void sendText() {
        chat.appendText("Spotter: " + chatField.getText() + "\n");
        chatField.setText("");
    }

    @FXML
    public void sendCoordinates() {

    }

    @FXML
    public void sendWind() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        coordinates.setText(OUTSIDE_TEXT);
        scope = new Image("/crosshair.png");
        mark = new ImageView(new Image("/red-x.png"));
        background.getChildren().setAll(createImage("/sample-1.jpg"));

        wind.setText(new Wind().toString());
        chatField.setOnAction(event -> sendText());
        startTime = System.currentTimeMillis();
        Timeline timer = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            long millis = System.currentTimeMillis() - startTime;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
            millis -= TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis));
            String text = String.format("%d:%02d:%03d", minutes, seconds, millis);
            time.setText(text);
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    private ImageView createImage(String url) {
        final Image img = new Image(url);
        final ImageView imgView = new ImageView(img);
        imgView.setFitHeight(img.getHeight()/2);
        imgView.setFitWidth(img.getWidth()/2);
        imgView.setCursor(new ImageCursor(scope, scope.getWidth()/2, scope.getHeight()/2));

        imgView.setOnMouseMoved(event -> coordinates.setText("X: " + (int) event.getX()/10 + " Y: " + (int) event.getY()/10));

        imgView.setOnMouseExited(event -> coordinates.setText(OUTSIDE_TEXT));

        imgView.setOnMouseClicked(event -> {
            //targetLabel.setText("Target locked at:\n" + "X: " + (int) event.getX()/10 + ", Y: " + (int) event.getY()/10);
            background.getChildren().remove(mark);
            Dimension2D dim = ImageCursor.getBestSize(event.getScreenX(), event.getScreenY());
            mark.setFitHeight(dim.getHeight());
            mark.setFitWidth(dim.getWidth());
            mark.setLayoutX(event.getX() - mark.getFitWidth() / 2);
            mark.setLayoutY(event.getSceneY() - mark.getFitHeight());
            background.getChildren().add(mark);
        });


        return imgView;
    }
}
