package http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Connection implements Runnable {
    
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
    
    // start_line = "Request-Line" | "Status-Line"
    public byte[] createHTTPMessage(String start_line, ArrayList<String> headers, String body) {
        // Calcul taille du message
        int size = start_line.length();
        for(int i = 0; i < headers.size(); i++)
            size += headers.get(i).length();
        size += 2; // CR LF characters
        size += body.length();
        
        byte[] message = new byte[size];
        int counter = 0;
        
        for(int i = 0; i < start_line.length(); i++)
            message[counter++] = start_line.getBytes()[i];
        
        for(int i = 0; i < headers.size(); i++)
            for(int j = 0; j < headers.get(i).length(); j++)
                message[counter++] = headers.get(i).getBytes()[j];
        
        message[counter++] = 13; // CR
        message[counter++] = 10; // LF
        
        for(int i = 0; i < body.length(); i++)
            message[counter++] = body.getBytes()[i];
        
        return message;
    }
}
