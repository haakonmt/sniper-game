package no.social.snipergame.model.asset;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 12.04.2016 10.49.
 */
public class Pants extends CharacterAsset {
    public Pants(Sex sex) {
        super(sex);
    }

    public Pants(String id) {
        super(id);
    }

    @Override
    String[] getLegalColors(String type) {
        return new String[]{"Black", "Blue", "Brown", "Green", "Grey", "Orange", "Purple", "Red", "Yellow"};
    }

    @Override
    String[] getLegalTypes() {
        return new String[]{""};
    }

    @Override
    String getUrl() {
        return "pants/" + super.getUrl();
    }

    @Override
    protected String getSeparator() {
        return "";
    }
}
