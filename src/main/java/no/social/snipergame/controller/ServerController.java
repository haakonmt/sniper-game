package no.social.snipergame.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import no.social.snipergame.model.Client;
import no.social.snipergame.model.Game;
import no.social.snipergame.util.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 18.04.2016 11.19.
 */
public class ServerController implements Initializable {

    private static Long currentId = 1L;

    @FXML private ListView<Client> clientListView;
    @FXML private Label ipLabel;
    @FXML private Label portLabel;
    @FXML private TextArea statusArea;

    private ServerSocket serverSocket;
    private final Map<String, ServerSocketAcceptedThread> clientMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Auto scroll to bottom
        statusArea.textProperty().addListener((observable, oldValue, newValue) -> statusArea.setScrollTop(Double.MAX_VALUE));
        ipLabel.setText(getIpAddress());

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.setDaemon(true); //terminate the thread when program end
        socketServerThread.start();
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            try {
                Socket socket;

                serverSocket = new ServerSocket(SocketServerPORT);
                Platform.runLater(() -> portLabel.setText(String.valueOf(serverSocket.getLocalPort())));

                while (true) {
                    socket = serverSocket.accept();
                    count++;

                    //Start another thread
                    //to prevent blocked by empty dataInputStream
                    Thread acceptedThread = new Thread(new ServerSocketAcceptedThread(socket, count));
                    acceptedThread.setDaemon(true); //terminate the thread when program end
                    acceptedThread.start();
                }
            } catch (IOException ex) {
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class ServerSocketAcceptedThread extends Thread {

        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        final int count;
        final Gson gson;

        ServerSocketAcceptedThread(Socket s, int c) {
            socket = s;
            count = c;
            gson = new GsonBuilder().create();
        }

        @Override
        public void run() {
            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                //If dataInputStream empty,
                //this thread will be blocked by readUTF(),
                //but not the others
                String messageFromClient = dataInputStream.readUTF();

                String newMessage = "#" + count + " from " + socket.getInetAddress()
                        + ":" + socket.getPort() + "\n"
                        + "Msg from client: " + messageFromClient + "\n";

                Client client = gson.fromJson(messageFromClient, Client.class);
                clientMap.put(client.getNickName(), this);

                Platform.runLater(() -> {
                    statusArea.appendText(newMessage);
                    clientListView.getItems().add(client);
                    if (count >= 2) {
                        Game game = startGameIfPossible();
                        if (game != null) {
                            String messageToSniper = gson.toJson(game.toSniper());
                            String messageToSpotter = gson.toJson(game.toSpotter());
                            try {
                                clientMap.get(game.getSpotter().getNickName()).dataOutputStream.writeUTF(messageToSpotter);
                                clientMap.get(game.getSniper().getNickName()).dataOutputStream.writeUTF(messageToSniper);
                            } catch (IOException e) {
                                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, e);
                            }
                            statusArea.appendText("Msg to client: " + messageToSniper + "\n");
                        }
                    }
                });

            } catch (IOException ex) {
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Game startGameIfPossible() {
        List<Client> cList = clientListView.getItems();
        for (int i = 0; i < cList.size(); i++) {
            for (int j = i+1; j < cList.size(); j++) {
                Client c1 = cList.get(i), c2 = cList.get(j);
                if (c1.getType() != c2.getType() && c1.getPreferredDifficulty() == c2.getPreferredDifficulty()) {
                    Client sniper = c1.getType() == Constants.PlayerType.SNIPER ? c1 : c2;
                    Client spotter = c1.getType() == Constants.PlayerType.SPOTTER ? c1 : c2;
                    clientListView.getItems().removeAll(c1, c2);
                    return new Game(currentId++, c1.getPreferredDifficulty(), sniper, spotter);
                }
            }
        }
        return null;
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumINetAddress = networkInterface
                        .getInetAddresses();
                while (enumINetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumINetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ip;
    }
}
