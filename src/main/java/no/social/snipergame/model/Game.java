package no.social.snipergame.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Random;

import static no.social.snipergame.util.Constants.BOARD_SIZE;
import static no.social.snipergame.util.Constants.Difficulty;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 10.29.
 */
@Data
public class Game {

    private final Long id;

    private final Difficulty difficulty;
    private final LocalDateTime startTime;
    private final Person[] persons = new Person[BOARD_SIZE];
    private Person targetPerson;
    private final Wind wind;
    private int winX, winY;
    private boolean isSniper, won = false;
    private final String tile;
    private final long startMillis;

    private final Client sniper, spotter;

    public Game(Long id, Difficulty difficulty, Client sniper, Client spotter) {
        this.id = id;
        this.sniper = sniper;
        this.spotter = spotter;
        this.difficulty = difficulty;
        wind = Wind.random();
        startTime = LocalDateTime.now();
        startMillis = System.currentTimeMillis();
        Random random = new Random();
        int target = random.nextInt(persons.length);
        for (int i = 0; i < persons.length; i++) {
            if (i == target) {
                winY = i/23;
                winX = i%23;
                System.out.println(winX + " " + winY);
                persons[i] = targetPerson = new Person(true);
            }
            else persons[i] = random.nextBoolean() ? new Person(false) : null;
        }
        tile = new String[]{"brick", "desert", "grass", "ground", "ground2"}[random.nextInt(5)];
    }

    public Game toSniper() {
        isSniper = true;
        return this;
    }

    public Game toSpotter() {
        isSniper = false;
        return this;
    }

    @Override
    public String toString() {
        return difficulty + " - " + startTime;
    }
}
