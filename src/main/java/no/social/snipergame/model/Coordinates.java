package no.social.snipergame.model;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 07.04.2016 10.19.
 */
public class Coordinates {

    private int x, y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "X: " + x + " Y: " + y;
    }
}
