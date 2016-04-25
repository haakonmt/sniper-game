package no.social.snipergame.network;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 21.04.2016 13.32.
 */
public class SpotterConnection extends NetworkConnection {

    private final String ip;
    private final int port;

    public SpotterConnection(String ip, int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected boolean isSniper() {
        return false;
    }

    @Override
    protected String getIp() {
        return ip;
    }

    @Override
    protected int getPort() {
        return port;
    }
}
