package no.social.snipergame.model;

import lombok.Data;
import no.social.snipergame.util.Constants.Direction;

import java.util.Random;

import static no.social.snipergame.util.Constants.Direction.*;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 01.02.2016 16.13.
 */
@Data
public class Wind {

    private final int speed;
    private final Direction direction;

    public Wind(int speed, Direction direction) {
        this.speed = speed;
        this.direction = direction;
    }

    public static Wind random() {
        return new Wind(new Random().nextInt(4), pickDirection(Math.random()));
    }

    private static Direction pickDirection(double random) {
        return random < 0.125 ? NORTH_EAST
                : random < 0.25 ? EAST
                : random < 0.375 ? SOUTH_EAST
                : random < 0.5 ? SOUTH
                : random < 0.625 ? SOUTH_WEST
                : random < 0.75 ? WEST
                : random < 0.875 ? NORTH_WEST
                : NORTH;
    }

    @Override
    public String toString() {
        return speed + " m/s, " + direction;
    }
}
