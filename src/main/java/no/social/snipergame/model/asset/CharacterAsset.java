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

    private String id = "";

    public enum Sex {
        MALE, FEMALE
    }

    final Sex sex;

    private final String type, color;

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
        id += sex + "-";
        type = getRandomStringFrom(getLegalTypes());
        id += "-";
        color = getRandomStringFrom(getLegalColors(type));
        setImage(new Image(IMAGE_PREFIX + getUrl()));
        setViewport(new Rectangle2D(40, 0, 40, 40));
    }

    CharacterAsset(String id) {
        String[] parts = id.split("-");
        sex = Sex.valueOf(parts[0]);
        type = getLegalTypes()[Integer.valueOf(parts[1])];
        color = getLegalColors(type)[Integer.valueOf(parts[2])];
        setImage(new Image(IMAGE_PREFIX + getUrl()));
        setViewport(new Rectangle2D(40, 0, 40, 40));
    }

    private String getRandomStringFrom(String... items) {
        int index = random.nextInt(items.length);
        id += index;
        return items[index];
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public String getAssetId() {
        return id;
    }
}
