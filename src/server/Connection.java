package server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Connection implements Runnable {
    
    public static final byte CR = 13;
    public static final byte LF = 10;
    
    protected Socket socket;
    protected InputStream in;
    protected OutputStream out;
    protected BufferedInputStream bufIn;

    public Connection(Socket connexion) {
        socket = connexion;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            bufIn = new BufferedInputStream(in);
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
