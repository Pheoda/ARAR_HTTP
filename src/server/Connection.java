package server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
    
    public String readFirstLine() {
        int character, counter = 0;
        boolean receivedCR = false, continueStartLine = true;
        String request = new String();

        do {
            try {
                character = bufIn.read();
                
                //System.out.print((char)character);
                if (continueStartLine) {
                    request += (char)character;
                }
                
                if (character == CR) {
                    receivedCR = true;
                } else if (character != LF) {
                    receivedCR = false;
                    counter = 0;
                }
                if (character == LF && receivedCR) {
                    receivedCR = false;
                    continueStartLine = false; // On arrête la lecture de la première ligne
                    counter++;
                }
            } catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (counter != 2); // Fin par double CR LF

        return request;
    }
    
    public String readBuffer() {
        int character, counter = 0;
        boolean receivedCR = false;
        String request = new String();

        do {
            try {
                character = bufIn.read();
                
                request += (char)character;
                
                if (character == CR) {
                    receivedCR = true;
                } else if (character != LF) {
                    receivedCR = false;
                    counter = 0;
                }
                if (character == LF && receivedCR) {
                    receivedCR = false;
                    counter++;
                }
            } catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (counter != 2); // Fin par double CR LF

        return request;
    }
}
