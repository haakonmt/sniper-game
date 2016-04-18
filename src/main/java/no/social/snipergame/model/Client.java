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
    private Difficulty prefferedDifficulty;

    private Game game;

    public Client(String nickName, PlayerType type, Difficulty prefferedDifficulty) {
        this.nickName = nickName;
        this.type = type;
        this.prefferedDifficulty = prefferedDifficulty;
    }

    @Override
    public String toString() {
        return "[" + prefferedDifficulty + "][" + type + "] " + nickName;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getNickName() {
        return nickName;
    }

    public PlayerType getType() {
        return type;
    }

    public Difficulty getPrefferedDifficulty() {
        return prefferedDifficulty;
    }
}
