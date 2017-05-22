package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private ServerSocket server;
    
    public Server() {
        try {
            server = new ServerSocket(1026, 6);
            Socket connexion = server.accept();
            new Thread(new WebConnection(connexion)).start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        
    }
    
}
