package http;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebConnection extends Connection {

    public WebConnection(Socket connexion) {
        super(connexion);
    }

    @Override
    public void run() {
        while(true) {
            try {
                // Lecture de la requête envoyée par le client
                bufIn.read();
                
                // Réponse envoyée au client
                out.write();
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(WebConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
