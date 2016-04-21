package no.social.snipergame.model;

import static no.social.snipergame.util.Constants.Difficulty;
import static no.social.snipergame.util.Constants.PlayerType;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 18.04.2016 12.36.
 */
public class Client {

    private String nickName;
    private PlayerType type;
    private Difficulty preferredDifficulty;

    public Client(String nickName, PlayerType type, Difficulty preferredDifficulty) {
        this.nickName = nickName;
        this.type = type;
        this.preferredDifficulty = preferredDifficulty;
    }

    @Override
    public String toString() {
        return "[" + preferredDifficulty + "][" + type + "] " + nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public PlayerType getType() {
        return type;
    }

    public Difficulty getPreferredDifficulty() {
        return preferredDifficulty;
    }
}
