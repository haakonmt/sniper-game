package no.social.snipergame.model.asset;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 12.04.2016 10.49.
 */
public class Hair extends CharacterAsset {

    public Hair(Sex sex) {
        super(sex);
    }

    @Override
    String[] getLegalColors(String type) {
        if (sex == Sex.MALE) {
            switch (type) {
                case "Afro": return new String[]{"Black", "Gold", "Grey", "NavyBlue", "Orange", "Red"};
                case "Balding": return new String[]{"Black", "Brown", "Gold", "Grey", "LightGrey", "NavyBlue", "Orange",
                        "Red", "White"};
                case "RegularCut": return new String[]{"Brown", "Gold", "Grey", "NavyBlue", "Orange", "Red"};
                case "Spiked": return new String[]{"Black", "Blonde", "BrightGreen", "Brown", "Gold", "NavyBlue", "Orange",
                        "Purple", "Red", "Turquoise", "White"};
            }
        }
        else if (sex == Sex.FEMALE) {
            switch (type) {
                case "Bun":
                    return new String[]{"Black", "Blonde", "Brown", "DarkGrey", "Golden", "LightGrey",
                            "NavyBlue", "Orange", "Red", "White"};
                case "LongWavy":
                    return new String[]{"Black", "Blonde", "BrightGreen", "Brown", "Golden", "Grey",
                            "NavyBlue", "Orange", "Purple", "Red", "Turquoise", "White"};
                case "PonyTail":
                case "PonyTailMessy":
                    return new String[]{"Blonde", "Golden", "Green_Blue", "Grey", "LightBlue", "NavyBlue", "Orange",
                            "Pink", "Purple", "Red", "White"};
                case "Straight":
                    return new String[]{"Black", "Blonde", "BrightGreen", "DirtyWhite", "Golden", "Grey", "Orange",
                            "Pink", "Purple", "Red", "Turquoise"};
            }
        }
        return null;
    }

    @Override
    String[] getLegalTypes() {
        return sex == Sex.MALE ? new String[]{"Afro", "Balding", "RegularCut", "Spiked"}
                : sex == Sex.FEMALE ? new String[]{"Bun", "LongWavy", "PonyTail", "PonyTailMessy", "Straight"}
                : null;
    }

    @Override
    String getUrl() {
        return sex.toString().toLowerCase() + "/hair/" + super.getUrl();
    }

    @Override
    protected String getSeparator() {
        return "-";
    }
}
