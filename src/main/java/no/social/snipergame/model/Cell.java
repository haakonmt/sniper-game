package no.social.snipergame.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Random;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 16.03.
 */
public class Cell extends StackPane {

    private final boolean target, empty;
    private final ImageView backGround;
    private final Person person;

    private static final Random random = new Random();

    public Cell(boolean target) {
        this.target = target;
        empty = !target && random.nextBoolean();
        person = !target ? empty ? null : new Person() : new Person();
        backGround = new ImageView("/icons/grass.png");
        getChildren().addAll(backGround);
        if (person != null) getChildren().addAll(person);
    }

    public boolean isEmpty() {
        return empty;
    }

    public ImageView getBackGround() {
        return backGround;
    }

    public Person getPerson() {
        return person;
    }

    public boolean isTarget() {
        return target;
    }
}
