package no.social.snipergame.model;

import javafx.animation.Timeline;
import no.social.snipergame.Client;

import java.util.List;

import static no.social.snipergame.Constants.Difficulty;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 10.29.
 */
public class Game {

    private long id;
    private Timeline timer;
    private Difficulty difficulty;
    private Client sniper, spotter;
    private List<Person> persons;

    public Game(Client sniper, Client spotter) {
        this.sniper = sniper;
        this.spotter = spotter;
    }

    public Client getSniper() {
        return sniper;
    }

    public void setSniper(Client sniper) {
        this.sniper = sniper;
    }

    public Client getSpotter() {
        return spotter;
    }

    public void setSpotter(Client spotter) {
        this.spotter = spotter;
    }
}
