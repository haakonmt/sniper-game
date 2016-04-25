package no.social.snipergame.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 21.04.2016 13.31.
 */
public abstract class NetworkConnection {

    private final ConnectionThread connectionThread = new ConnectionThread();
    private final Consumer<Serializable> onReceiveCallback;

    NetworkConnection(Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
        connectionThread.setDaemon(true);
    }

    public void startConnection() {
        connectionThread.start();
    }

    public void send(Serializable data) throws Exception {
        connectionThread.out.writeObject(data);
    }

    public void closeConnection() throws Exception {
        connectionThread.socket.close();
    }

    protected abstract boolean isSniper();
    protected abstract String getIp();
    protected abstract int getPort();

    private class ConnectionThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;

        @Override
        public void run() {
            try (ServerSocket server = isSniper() ? new ServerSocket(getPort()) : null;
                 Socket socket = isSniper() ? server.accept() : new Socket(getIp(), getPort());

                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);

                while (true) {
                    Serializable data = (Serializable) in.readObject();
                    onReceiveCallback.accept(data);
                }
            } catch (IOException | ClassNotFoundException e) {
                onReceiveCallback.accept("Connection closed.");
            }
        }
    }
}