package no.social.snipergame.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 16.05.
 */
public class Person extends StackPane {

    private final static Map<String, String[]> hairColorMap = new HashMap<>();
    private final static Map<String, String[]> shirtColorMap = new HashMap<>();

    static {
        // Female hair
        hairColorMap.put("Bun", new String[]{"Black", "Brown", "DarkGrey", "Golden",
                "LightBlonde", "LightGrey", "NavyBlue", "Orange", "Red", "White"});
        hairColorMap.put("LongWavy", new String[]{"Black", "Blonde", "BrightGreen", "Brown",
                "Golden", "Grey", "NavyBlue", "Orange", "Purple", "Red", "Turquoise", "White"});
        String[] ponyTailColors = new String[]{"Blonde", "Golden", "Green_Blue", "Grey",
                "LightBlue", "NavyBlue", "Orange", "Pink", "Purple", "Red", "White"};
        hairColorMap.put("PonyTail", ponyTailColors);
        hairColorMap.put("PonyTailMessy", ponyTailColors);
        hairColorMap.put("Straight", new String[]{"Black", "Blonde", "BrightGreen", "DirtyWhite", "Golden",
                "Grey", "Orange", "Pink", "Purple", "Red", "Turquoise"});

        // Male hair
        hairColorMap.put("Afro", new String[]{"Black", "Gold", "Grey", "NavyBlue", "Orange", "Red"});
        hairColorMap.put("Balding", new String[]{"Black", "Brown", "Gold", "Grey", "LightGrey",
                "NavyBlue", "Orange", "Red", "White"});
        hairColorMap.put("RegularCut", new String[]{"Brown", "Gold", "Grey", "NavyBlue", "Orange", "Red"});
        hairColorMap.put("Spiked", new String[]{"Black", "Blonde", "BrightGreen", "Brown", "Gold", "NavyBlue",
                "Orange", "Purple", "Red", "Turquoise", "White"});

        // Shirt color
        shirtColorMap.put("Plain", new String[]{"Black", "Blue", "DarkGreyBlue", "Green", "Grey", "LightPurple",
                "Orange", "Pink", "Red", "Turquoise", "White", "Yellow"});
        shirtColorMap.put("Striped", new String[]{"Blue", "DarkGreen", "Green", "Grey", "Purple", "Orange", "Pink", "Red", "Yellow"});
    }

    private final static String
            IMAGE_PREFIX = "/characters/", IMAGE_POSTFIX = ".png",

    MALE_FIRST_NAMES[] = new String[] {
            "James", "John", "Robert", "Michael", "William", "Steven", "Nicholas", "Gary",
            "David", "Richard", "Charles", "Joseph", "Thomas", "Christopher", "Daniel",
            "Paul", "Mark", "Donald", "George", "Justin", "Brian", "Anthony", "Jason",
            "Matthew", "Kevin", "Edward"
    },

    FEMALE_FIRST_NAMES[] = new String[]{
            "Mary", "Patricia", "Linda", "Barbara", "Elizabeth", "Jennifer", "Maria", "Susan",
            "Margaret", "Dorothy", "Lisa", "Nancy", "Karen", "Betty", "Helen", "Sandra", "Donna",
            "Carol", "Ruth", "Sharon", "Michelle", "Laura", "Sarah", "Kimberly", "Deborah",
            "Jessica", "Shirley", "Cynthia", "Angela", "Melissa"
    },

    LAST_NAMES[] = new String[]{
            "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson",
            "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin",
            "Thompson", "Garcia", "Robinson", "Clark", "Lewis", "Lee", "Walker", "Rodriguez",
            "Allen", "Stewart", "Sanchez", "Scott", "Green"
    };

    private String firstName, lastName;
    private String sex;
    private static final Random random = new Random();

    private ImageView base, hair, shirt, pants, shoes;

    public Person() {
        sex = getRandomStringFrom("male", "female");
        base = generateBase();
        hair = generateHair();
        shirt = generateShirt();
        pants = generatePants();
        shoes = generateShoes();
        getChildren().addAll(base, hair, shirt, pants, shoes);
        firstName = getRandomStringFrom(sex.equals("male") ? MALE_FIRST_NAMES : FEMALE_FIRST_NAMES);
        lastName = getRandomStringFrom(LAST_NAMES);
    }

    private ImageView generateShoes() {
        ImageView imageView = new ImageView(IMAGE_PREFIX + "shoes/Shoes-" +
                getRandomStringFrom("Black", "Blue", "DarkBrown", "Green", "Grey", "LightBrown",
                        "LightGrey", "Orange", "Purple", "Red", "Yellow") + IMAGE_POSTFIX);
        imageView.setViewport(new Rectangle2D(40, 0, 40, 40));
        return imageView;
    }

    private ImageView generateShirt() {
        String type = getRandomStringFrom("Plain", "Striped");
        String color = getRandomStringFrom(shirtColorMap.get(type));
        return generateImageView(IMAGE_PREFIX + sex + "/shirts/" + (sex.equals("male") ? "Guy" : "Girl")
                + "Shirt-" + type + "-" + color + IMAGE_POSTFIX);
    }

    private ImageView generatePants() {
        return generateImageView(IMAGE_PREFIX + "pants/Pants-" +
                getRandomStringFrom("Black", "Blue", "Brown", "Green", "Grey",
                        "Orange", "Purple", "Red", "Yellow") + IMAGE_POSTFIX);
    }

    private ImageView generateHair() {
        boolean male = sex.equals("male");
        String hairType = (male ?
                getRandomStringFrom("Afro", "Balding", "RegularCut", "Spiked") :
                getRandomStringFrom("Bun", "LongWavy", "PonyTail", "PonyTailMessy", "Straight"));
        return generateImageView(IMAGE_PREFIX + sex + "/hair/Hair-" + (male ? "Guy" : "Girl") + "-"
                + hairType + "-" + getRandomStringFrom(hairColorMap.get(hairType)) + IMAGE_POSTFIX);
    }

    private ImageView generateBase() {
        return generateImageView(IMAGE_PREFIX + sex + "/base/" + (sex.equals("male") ? "Guy" : "Girl")
                + "Base-" + getRandomStringFrom("Dark", "Med", "Light") + IMAGE_POSTFIX);
    }

    private ImageView generateImageView(String url) {
        ImageView imageView = new ImageView(url);
        imageView.setViewport(new Rectangle2D(40, 0, 40, 40));
        return imageView;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getConcatinatedName() {
        return firstName.substring(0,1) + ". " + lastName;
    }

    private static String getRandomStringFrom(String... items) {
        return items[random.nextInt(items.length)];
    }
}
