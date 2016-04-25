package no.social.snipergame.network;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 21.04.2016 13.32.
 */
public class SniperConnection extends NetworkConnection {

    private final int port;

    public SniperConnection(int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.port = port;
    }

    @Override
    protected boolean isSniper() {
        return true;
    }

    @Override
    protected String getIp() {
        return null;
    }

    @Override
    protected int getPort() {
        return port;
    }
}
