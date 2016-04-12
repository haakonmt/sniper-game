package no.social.snipergame.model.asset;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 12.04.2016 10.48.
 */
public abstract class CharacterAsset extends ImageView {

    public enum Sex {
        MALE, FEMALE
    }

    Sex sex;

    private String type, color;

    private static final String IMAGE_PREFIX = "/characters/", IMAGE_POSTFIX = ".png";

    private static final Random random = new Random();

    abstract String[] getLegalColors(String type);

    abstract String[] getLegalTypes();

    String getUrl() {
        return type + getSeparator() + color + IMAGE_POSTFIX;
    }

    protected abstract String getSeparator();

    CharacterAsset(Sex sex) {
        this.sex = sex;
        type = getRandomStringFrom(getLegalTypes());
        color = getRandomStringFrom(getLegalColors(type));
        setImage(new Image(IMAGE_PREFIX + getUrl()));
        setViewport(new Rectangle2D(40, 0, 40, 40));
    }

    private static String getRandomStringFrom(String... items) {
        return items[random.nextInt(items.length)];
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }
}
