package http;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebConnection extends Connection {

    public WebConnection(Socket connexion) {
        super(connexion);
    }
    
    public byte[] fillBody(String fileName, String value, String reason) {
        byte[] body;
        value = "200";
        reason = "OK";
        
        
        return body;
    }

    @Override
    public void run() {
        while(true) {
            try {
                // Lecture de la requête envoyée par le client
                int character, counter = 0;
                boolean receivedCR = false, continueStartLine = true;
                String request = new String();
                        
                do
                {
                    character = bufIn.read();
                    
                    //System.out.print((char)character);
                    
                    if(continueStartLine)
                        request += (char)character;
                    
                    if(character == CR)
                        receivedCR = true;
                    else if(character != LF) {
                        receivedCR = false;
                        counter = 0;
                    }
                    if(character == LF && receivedCR) {
                        receivedCR = false;
                        continueStartLine = false; // On arrête la lecture de la première ligne
                        counter++;
                    }
                }while(counter != 2); // Fin par double CR LF
                
                // Analyse requête reçue
                String[] strings = request.split(" "); // Découpage de la requête
                
                ArrayList<String> headers = new ArrayList<>();

                switch(strings[0]) {
                    case "GET" :
                        // On traite la ressource demandée
                        if(strings[1].equals("/")) // On demande index.html
                            strings[1] = "/index.html";
                        
                        String[] splitFile = strings[1].split("\\.");
                        String extension = splitFile[splitFile.length-1];
                        switch(extension) {
                            case "html":
                                headers.add("Content-Type: text/html");
                                break;
                            case "png":
                                headers.add("Content-Type: image/png");
                                break;
                            case "jpg":
                                headers.add("Content-Type: image/jpg");
                                break;
                        }
                        break;
                    case "HEAD":
                        break;
                }
                
                String returnValue = new String();
                String returnReason = new String();
                
                byte[] body = fillBody(strings[1], returnValue, returnReason);
                // CreateResponseStartLine
                // Sinon, parcourir fichier, remplir body
                
                // Réponse envoyée au client
                byte[] startLine = createResponseStartLine("HTTP/1.1", returnValue, returnReason);
                /*byte[] body = ("<!doctype html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <meta charset=\"utf-8\">\n" +
                    "  <title>Hello</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h1>LE SUPER SITE D'ALAIN</h1>" +
                    "<p><img src='images/alain.png'/></p>\n" +
                    "</body>\n" +
                    "</html>").getBytes();*/
                
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
