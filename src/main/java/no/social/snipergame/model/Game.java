package no.social.snipergame.model;

import no.social.snipergame.ClientProgram;

import java.time.LocalDateTime;
import java.util.List;

import static no.social.snipergame.Constants.Difficulty;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 10.29.
 */
public class Game {

    private long id;
    private Difficulty difficulty;
    private LocalDateTime startTime;
    private ClientProgram sniper, spotter;
    private List<Person> persons;

    public Game(ClientProgram sniper, ClientProgram spotter) {
        this.sniper = sniper;
        this.spotter = spotter;
        startTime = LocalDateTime.now();
    }

    public ClientProgram getSniper() {
        return sniper;
    }

    public void setSniper(ClientProgram sniper) {
        this.sniper = sniper;
    }

    public ClientProgram getSpotter() {
        return spotter;
    }

    public void setSpotter(ClientProgram spotter) {
        this.spotter = spotter;
    }

    @Override
    public String toString() {
        return difficulty + " - " + startTime;
    }
}
