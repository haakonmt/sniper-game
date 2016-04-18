package no.social.snipergame.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import no.social.snipergame.ServerProgram;
import no.social.snipergame.model.Client;
import no.social.snipergame.model.Game;
import no.social.snipergame.util.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 18.04.2016 11.19.
 */
public class ServerController implements Initializable {

    @FXML private CheckBox welcomeCheckBox;
    @FXML private ListView<Client> clientListView;
    @FXML private ListView<Game> gameListView;
    @FXML private Label ipLabel;
    @FXML private Label portLabel;
    @FXML private TextArea statusArea;

    private ServerSocket serverSocket;

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
                    Thread acceptedThread = new Thread(
                            new ServerSocketAcceptedThread(socket, count));
                    acceptedThread.setDaemon(true); //terminate the thread when program end
                    acceptedThread.start();

                }
            } catch (IOException ex) {
                Logger.getLogger(ServerProgram.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    private class ServerSocketAcceptedThread extends Thread {

        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        int count;
        Gson gson;

        ServerSocketAcceptedThread(Socket s, int c) {
            socket = s;
            count = c;
            gson = new GsonBuilder().create();
        }

        @Override
        public void run() {
            try {
                dataInputStream = new DataInputStream(
                        socket.getInputStream());
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());

                //If dataInputStream empty,
                //this thread will be blocked by readUTF(),
                //but not the others
                String messageFromClient = dataInputStream.readUTF();

                String newMessage = "#" + count + " from " + socket.getInetAddress()
                        + ":" + socket.getPort() + "\n"
                        + "Msg from client: " + messageFromClient + "\n";

                Client client = gson.fromJson(messageFromClient, Client.class);
                clientListView.getItems().add(client);

                if (clientListView.getItems().size() >= 2) {
                    Game game = startGameIfPossible();
                    if (game != null) {
                        dataOutputStream.writeUTF(gson.toJson(game));
                    }
                }

                Platform.runLater(() -> statusArea.appendText(newMessage));

                if (welcomeCheckBox.isSelected()) {

                    String msgReply = count + ": " + "Connected!";
                    dataOutputStream.writeUTF(msgReply);
                }

            } catch (IOException ex) {
                Logger.getLogger(ServerProgram.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ServerProgram.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ServerProgram.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ServerProgram.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private Game startGameIfPossible() {
        List<Client> cList = clientListView.getItems();
        for (int i = 0; i < cList.size(); i++) {
            for (int j = i+1; j < cList.size(); j++) {
                Client c1 = cList.get(i), c2 = cList.get(j);
                if (c1.getType() != c2.getType() && c1.getPrefferedDifficulty() == c2.getPrefferedDifficulty()) {
                    Client sniper = c1.getType() == Constants.PlayerType.SNIPER ? c1 : c2;
                    Client spotter = c1.getType() == Constants.PlayerType.SPOTTER ? c1 : c2;
                    Game game = new Game(c1.getPrefferedDifficulty(), sniper, spotter);
                    gameListView.getItems().add(game);
                    c1.setGame(game);
                    c1.setGame(game);
                    clientListView.getItems().removeAll(c1, c2);
                    return game;
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
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(ServerProgram.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ip;
    }
}
