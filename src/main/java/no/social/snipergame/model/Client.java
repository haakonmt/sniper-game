package no.social.snipergame.model;

import lombok.Data;

import static no.social.snipergame.util.Constants.Difficulty;
import static no.social.snipergame.util.Constants.PlayerType;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 18.04.2016 12.36.
 */
@Data
public class Client {

    private final String nickName;
    private final PlayerType type;
    private final Difficulty preferredDifficulty;

    public Client(String nickName, PlayerType type, Difficulty preferredDifficulty) {
        this.nickName = nickName;
        this.type = type;
        this.preferredDifficulty = preferredDifficulty;
    }

    @Override
    public String toString() {
        return "[" + preferredDifficulty + "][" + type + "] " + nickName;
    }
}
