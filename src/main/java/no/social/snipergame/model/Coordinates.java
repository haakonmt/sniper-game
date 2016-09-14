package no.social.snipergame.model;

import lombok.Data;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 07.04.2016 10.19.
 */
@Data
public class Coordinates {

    private int x, y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "X: " + x + " Y: " + y;
    }
}
