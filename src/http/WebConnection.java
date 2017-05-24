package http;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
                int character, counter = 0;
                boolean receivedCR = false;
                String request = new String();
                        
                do
                {
                    character = bufIn.read();
                    System.out.print((char)character);
                    
                    if(character == CR)
                        receivedCR = true;
                    else if(character != LF) {
                        receivedCR = false;
                        counter = 0;
                    }
                    if(character == LF && receivedCR) {
                        receivedCR = false;
                        counter++;
                    }
                }while(counter != 2); // Fin par double CR LF
                
                // Analyse requête reçue
                String[] strings = request.split(" "); // Découpage de la requête
                switch(strings[0]) {
                    case "GET" :
                        break;
                }
                
                // Réponse envoyée au client
                byte[] startLine = createResponseStartLine("HTTP/1.1", "200", "OK");
                ArrayList<String> headers = new ArrayList<>();
                byte[] body = ("<!doctype html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <meta charset=\"utf-8\">\n" +
                    "  <title>Hello</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h1>LE SUPER SITE D'ALAIN</h1>" +
                    "<p><img src='images/alain.png'/></p>\n" +
                    "</body>\n" +
                    "</html>").getBytes();
                headers.add("Content-Type: text/html");
                
                byte[] message = createHTTPMessage(startLine, headers, body);
                /*System.out.println();
                System.out.println("REPONSE : ");
                System.out.println(new String(message));*/
                out.write(message);
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(WebConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
