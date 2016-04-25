package no.social.snipergame.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 18.04.2016 14.04.
 */
public class Message {

    private final Long gameId;
    private final Client receiver, sender;
    private final String text;
    private final Date timeStamp;

    public Message(Long gameId, Client sender, Client receiver, String text) {
        this.gameId = gameId;
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        timeStamp = new Date();
    }

    @Override
    public String toString() {
        return "[" + new SimpleDateFormat("HH:MM:ss").format(timeStamp) + "] " + sender.getNickName() + ": " + text;
    }
}
