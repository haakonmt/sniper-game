package no.social.snipergame.model.asset;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 12.04.2016 10.49.
 */
public class Shoes extends CharacterAsset {

    public Shoes(Sex sex) {
        super(sex);
    }

    public Shoes(String id) {
        super(id);
    }

    @Override
    String[] getLegalColors(String type) {
        return new String[]{"Black", "Blue", "DarkBrown", "Green", "Grey", "LightBrown", "LightGrey", "Orange",
                "Purple", "Red", "Yellow"};
    }

    @Override
    String[] getLegalTypes() {
        return new String[]{""};
    }

    @Override
    String getUrl() {
        return "shoes/" + super.getUrl();
    }

    @Override
    protected String getSeparator() {
        return "";
    }
}
