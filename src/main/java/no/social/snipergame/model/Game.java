package no.social.snipergame.model;

import javafx.animation.Timeline;

import java.time.LocalDateTime;
import java.util.Random;

import static no.social.snipergame.util.Constants.Difficulty;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 10.29.
 */
public class Game {

    private Difficulty difficulty;
    private LocalDateTime startTime;
    private final Person[] persons = new Person[23*16];
    private Wind wind;
    private Timeline timer;
    private int winX, winY;
    private boolean isSniper;
    private String tile;

    private Client sniper, spotter;

    public Game(Difficulty difficulty, Client sniper, Client spotter) {
        this.sniper = sniper;
        this.spotter = spotter;
        this.difficulty = difficulty;
        wind = Wind.random();
        startTime = LocalDateTime.now();
        Random random = new Random();
        int target = random.nextInt(persons.length);
        for (int i = 0; i < persons.length; i++) {
            if (i == target) persons[i] = new Person(true);
            else persons[i] = random.nextBoolean() ? new Person(false) : null;
        }
        tile = new String[]{"brick", "desert", "grass", "ground", "ground2"}[random.nextInt(5)];

        /*
        long milliStart = System.currentTimeMillis();
        timer = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            long millis = System.currentTimeMillis() - milliStart;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
            millis -= TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis));
            String sendThisSomewhere = String.format("%d:%02d:%03d", minutes, seconds, millis);
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
        */
    }

    public Person[] getPersons() {
        return persons;
    }

    public Client getSpotter() {
        return spotter;
    }

    public Client getSniper() {
        return sniper;
    }

    public Wind getWind() {
        return wind;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Game toSniper() {
        isSniper = true;
        return this;
    }

    public Game toSpotter() {
        isSniper = false;
        return this;
    }

    public boolean isSniper() {
        return isSniper;
    }

    @Override
    public String toString() {
        return difficulty + " - " + startTime;
    }

    public String getTile() {
        return tile;
    }
}
