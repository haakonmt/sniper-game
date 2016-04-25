package no.social.snipergame.model;

import java.time.LocalDateTime;
import java.util.Random;

import static no.social.snipergame.util.Constants.Difficulty;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 10.29.
 */
public class Game {

    private final Long id;

    private final Difficulty difficulty;
    private final LocalDateTime startTime;
    private final Person[] persons = new Person[23*16];
    private Person targetPerson;
    private final Wind wind;
    private int winX, winY;
    private boolean isSniper, gameOver = false, won = false;
    private final String tile;
    private long startMillis;

    private final Client sniper;
    private final Client spotter;

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
                winX = i % 23;
                System.out.println(winX + " " + winY);
                persons[i] = new Person(true);
                targetPerson = persons[i];
            }
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

    public Long getId() {
        return id;
    }

    public int getWinX() {
        return winX;
    }

    public int getWinY() {
        return winY;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public Person getTargetPerson() {
        return targetPerson;
    }
}
