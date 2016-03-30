package no.social.snipergame.model;

import no.social.snipergame.Constants.Direction;

import static no.social.snipergame.Constants.Direction.*;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 01.02.2016 16.13.
 */
public class Wind {

    private final int speed;
    private final Direction direction;

    public Wind() {
        this.speed = (int) ((Math.random() < 0.5 ? 10 : 100) * Math.random()); // Returns an int between 0 and 100
        this.direction = pickDirection(Math.random());
    }

    public Wind(int speed, Direction direction) {
        this.speed = speed;
        this.direction = direction;
    }

    private Direction pickDirection(double random) {
        return random < 0.125 ? NORTH_EAST
                : random < 0.25 ? EAST
                : random < 0.375 ? SOUTH_EAST
                : random < 0.5 ? SOUTH
                : random < 0.625 ? SOUTH_WEST
                : random < 0.75 ? WEST
                : random < 0.875 ? NORTH_WEST
                : NORTH;
    }

    public int getSpeed() {
        return speed;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return speed + " m/s, " + direction;
    }
}
