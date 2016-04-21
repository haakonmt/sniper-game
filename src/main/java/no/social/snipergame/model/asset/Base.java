package no.social.snipergame.model.asset;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 12.04.2016 10.48.
 */
public class Base extends CharacterAsset {

    public Base(Sex sex) {
        super(sex);
    }

    public Base(String id) {
        super(id);
    }

    @Override
    String[] getLegalColors(String type) {
        return new String[]{"Light", "Med", "Dark"};
    }

    @Override
    String[] getLegalTypes() {
        return new String[]{""};
    }

    @Override
    String getUrl() {
        return sex.toString().toLowerCase() + "/base/" + super.getUrl();
    }

    @Override
    protected String getSeparator() {
        return "";
    }
}
