package no.social.snipergame.model.asset;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 12.04.2016 11.27.
 */
public class Shirt extends CharacterAsset {
    public Shirt(Sex sex) {
        super(sex);
    }

    public Shirt(String id) {
        super(id);
    }

    @Override
    String[] getLegalColors(String type) {
        switch (type) {
            case "Plain": return new String[]{"Black", "Blue", "DarkGreyBlue", "Green", "Grey", "LightPurple",
                    "Orange", "Pink", "Red", "Turquoise", "White", "Yellow"};
            case "Striped": return new String[]{"Blue", "DarkGreen", "Green", "Grey", "Orange", "Pink", "Purple", "Red", "Yellow"};
        }
        return null;
    }

    @Override
    String[] getLegalTypes() {
        return new String[]{"Plain", "Striped"};
    }

    @Override
    String getUrl() {
        return sex.toString().toLowerCase() + "/shirts/" + super.getUrl();
    }

    @Override
    protected String getSeparator() {
        return "-";
    }
}
