package no.social.snipergame.model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static no.social.snipergame.util.Constants.Difficulty;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 10.29.
 */
public class Game {

    private Difficulty difficulty;
    private LocalDateTime startTime;
    private final Person[] persons = new Person[24*17];
    private Wind wind;
    private Timeline timer;
    private int winX, winY;

    public Game(Difficulty difficulty) {
        this.difficulty = difficulty;
        wind = Wind.random();
        startTime = LocalDateTime.now();
        Random random = new Random();
        int target = random.nextInt(persons.length);
        for (int i = 0; i < persons.length; i++) {
            if (i == target) persons[i] = new Person(true);
            else persons[i] = random.nextBoolean() ? new Person(false) : null;
        }

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

    @Override
    public String toString() {
        return difficulty + " - " + startTime;
    }
}
