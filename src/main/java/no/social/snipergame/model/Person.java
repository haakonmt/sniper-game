package no.social.snipergame.model;

import javafx.scene.layout.StackPane;
import no.social.snipergame.model.asset.*;
import no.social.snipergame.model.asset.CharacterAsset.Sex;

import java.util.Random;

import static no.social.snipergame.model.asset.CharacterAsset.Sex.FEMALE;
import static no.social.snipergame.model.asset.CharacterAsset.Sex.MALE;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 16.05.
 */
public class Person extends StackPane {

    private static final String[]
            MALE_FIRST_NAMES = new String[] {
            "James", "John", "Robert", "Michael", "William", "Steven", "Nicholas", "Gary",
            "David", "Richard", "Charles", "Joseph", "Thomas", "Christopher", "Daniel",
            "Paul", "Mark", "Donald", "George", "Justin", "Brian", "Anthony", "Jason",
            "Matthew", "Kevin", "Edward"
    },
            FEMALE_FIRST_NAMES = new String[]{
                    "Mary", "Patricia", "Linda", "Barbara", "Elizabeth", "Jennifer", "Maria", "Susan",
                    "Margaret", "Dorothy", "Lisa", "Nancy", "Karen", "Betty", "Helen", "Sandra", "Donna",
                    "Carol", "Ruth", "Sharon", "Michelle", "Laura", "Sarah", "Kimberly", "Deborah",
                    "Jessica", "Shirley", "Cynthia", "Angela", "Melissa"
            },
            LAST_NAMES = new String[]{
                    "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson",
                    "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin",
                    "Thompson", "Garcia", "Robinson", "Clark", "Lewis", "Lee", "Walker", "Rodriguez",
                    "Allen", "Stewart", "Sanchez", "Scott", "Green"
            };

    private String firstName, lastName;
    private Sex sex;
    private static final Random random = new Random();
    private final boolean target;

    private CharacterAsset base, hair, shirt, pants, shoes;

    public Person(boolean target) {
        this.target = target;
        sex = random.nextBoolean() ? MALE : FEMALE;
        base = new Base(sex);
        hair = new Hair(sex);
        shirt = new Shirt(sex);
        pants = new Pants(sex);
        shoes = new Shoes(sex);
        getChildren().addAll(base, hair, shirt, pants, shoes);
        firstName = getRandomStringFrom(sex == MALE ? MALE_FIRST_NAMES : FEMALE_FIRST_NAMES);
        lastName = getRandomStringFrom(LAST_NAMES);
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

    public boolean isTarget() {
        return target;
    }

    public Sex getSex() {
        return sex;
    }

    public CharacterAsset getBase() {
        return base;
    }

    public CharacterAsset getHair() {
        return hair;
    }

    public CharacterAsset getShirt() {
        return shirt;
    }

    public CharacterAsset getPants() {
        return pants;
    }

    public CharacterAsset getShoes() {
        return shoes;
    }
}
