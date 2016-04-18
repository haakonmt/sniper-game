package no.social.snipergame.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 18.04.2016 14.04.
 */
public class Message {

    private final String sender, text;
    private final Date timeStamp;

    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
        timeStamp = new Date();
    }

    @Override
    public String toString() {
        return "[" + new SimpleDateFormat("dd.MM.yy - HH:MM:ss").format(timeStamp) + "] " + sender + ": " + text;
    }
}
