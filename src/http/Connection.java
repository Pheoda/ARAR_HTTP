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
        
    public byte[] createRequestStartLine() {
        return null;
    }
    
    public byte[] createResponseStartLine(String version, String code, String reason) {
        int size = version.length() + 1 + code.length() + 1 + reason.length() + 2;
        byte[] message = new byte[size];
        
        int counter = 0;
        
        for(int i = 0; i < version.length(); i++)
            message[counter++] = version.getBytes()[i];
                
        message[counter++] = (byte)' ';
        
        for(int i = 0; i < code.length(); i++)
            message[counter++] = code.getBytes()[i];
                
        message[counter++] = (byte)' ';
        
        for(int i = 0; i < reason.length(); i++)
            message[counter++] = reason.getBytes()[i];
                
        message[counter++] = CR;
        message[counter++] = LF;
                
        return message;
    }

    public byte[] createHTTPMessage(byte[] start_line, ArrayList<String> headers, byte[] body) {
        // Calcul taille du message
        int size = start_line.length;
        for(int i = 0; i < headers.size(); i++)
            size += headers.get(i).length() + 2;
        size += 2; // CR LF characters
        size += body.length;
        
        byte[] message = new byte[size];
        int counter = 0;
        
        for(int i = 0; i < start_line.length; i++)
            message[counter++] = start_line[i];
        
        for(int i = 0; i < headers.size(); i++) {
            for(int j = 0; j < headers.get(i).length(); j++)
                message[counter++] = headers.get(i).getBytes()[j];
            message[counter++] = CR;
            message[counter++] = LF;
        }
        
        message[counter++] = CR;
        message[counter++] = LF;
        
        for(int i = 0; i < body.length; i++)
            message[counter++] = body[i];
        
        return message;
    }
}
