package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Connection;

public class Client extends Connection{
    
    Socket socket;
    
    public Client(InetAddress ia, int port) {
        try {
            socket = new Socket(ia, port);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        super(socket);
    }
    
    
    public static void main(String[] args) {
        Client c = new Client(new InetAddress(InetAddress.getByAddress("127.0.0.1".getBytes()), 2000);
        
        new Thread(c).start();
        
    }
            
    public byte[] createRequestStartLine() {
        return null;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
