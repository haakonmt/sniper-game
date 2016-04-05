package no.social.snipergame.network;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 05.04.2016 15.45.
 */
public class Client extends NetworkConnection {

    private String ip;
    private int port;

    public Client(String ip, int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected boolean isServer() {
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
