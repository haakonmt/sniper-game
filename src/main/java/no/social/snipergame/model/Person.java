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
public class Person {

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

    private final String firstName, lastName, base, hair, shirt, pants, shoes;
    private final Sex sex;
    private static final Random random = new Random();
    private final boolean target;

    public Person(boolean target) {
        this.target = target;
        sex = random.nextBoolean() ? MALE : FEMALE;
        base = new Base(sex).getAssetId();
        hair = new Hair(sex).getAssetId();
        shirt = new Shirt(sex).getAssetId();
        pants = new Pants(sex).getAssetId();
        shoes = new Shoes(sex).getAssetId();
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

    public String getBase() {
        return base;
    }

    public String getHair() {
        return hair;
    }

    public String getShirt() {
        return shirt;
    }

    public String getPants() {
        return pants;
    }

    public String getShoes() {
        return shoes;
    }

    public StackPane compile() {
        return new StackPane(
                new Base(base), new Hair(hair),
                new Shirt(shirt), new Pants(pants),
                new Shoes(shoes)
        );
    }
}
