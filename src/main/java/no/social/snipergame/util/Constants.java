package no.social.snipergame.util;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 22.02.2016 17.47.
 */
public class Constants {

    public static final String
            SERVER_PORT = "12338",
            SERVER_HOSTNAME = "m21.cloudmqtt.com",
            MQTT_USERNAME = "taxdexgp";
    public static final char[] MQTT_PASSWORD = "9XmcKUZQ2qaW".toCharArray();
    public static final int BOARD_SIZE = 23*16;

    public enum Difficulty {
        EASY, MEDIUM, HARD, VERY_HARD
    }

    public enum PlayerType {
        SNIPER, SPOTTER
    }

    public enum Direction {
        NORTH, SOUTH, EAST, WEST,
        NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST;

        @Override
        public String toString() {
            String[] parts = name().split("_");
            return String.valueOf(parts[0].charAt(0)) + (parts.length == 2 ? String.valueOf(parts[1].charAt(0)) : "");
        }
    }
}
