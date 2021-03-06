package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private ServerSocket server;
    private static final int PORT = 2000;
    private static final int CONNECTION = 6;

    public Server() {
        try {
            server = new ServerSocket(PORT, CONNECTION);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Server s = new Server();

        new Thread(s).start();

    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket connexion;
                connexion = server.accept();
                new Thread(new WebConnection(connexion)).start();

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
