package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebConnection extends Connection {

    private String value;
    private String reason;

    public WebConnection(Socket connexion) {
        super(connexion);
    }

    public byte[] fillBody(String fileName) {
        byte[] body;
        int counter;

        fileName = System.getProperty("user.dir") + fileName;
        File f = new File(fileName);

        System.out.println("Traitement : " + f.getAbsolutePath());

        if (!f.exists()) { // Erreur 404
            value = "404";
            reason = "File Not Found";
            return null;
        } else {
            value = "200";
            reason = "OK";

            body = new byte[(int) f.length()];

            try {
                FileInputStream file = new FileInputStream(f);
                for (counter = 0; counter < (int) f.length(); counter++) {
                    body[counter] = (byte) file.read();
                }
            } catch (IOException ex) {
                Logger.getLogger(WebConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //System.out.println(new String(body));
        return body;
    }

    public byte[] createResponseStartLine(String version, String code, String reason) {
        int size = version.length() + 1 + code.length() + 1 + reason.length() + 2;
        byte[] message = new byte[size];

        int counter = 0;

        for (int i = 0; i < version.length(); i++) {
            message[counter++] = version.getBytes()[i];
        }

        message[counter++] = (byte) ' ';

        for (int i = 0; i < code.length(); i++) {
            message[counter++] = code.getBytes()[i];
        }

        message[counter++] = (byte) ' ';

        for (int i = 0; i < reason.length(); i++) {
            message[counter++] = reason.getBytes()[i];
        }

        message[counter++] = CR;
        message[counter++] = LF;

        return message;
    }

    public byte[] createHTTPMessage(byte[] start_line, ArrayList<String> headers, byte[] body) {
        // Calcul taille du message
        int size = start_line.length;
        for (int i = 0; i < headers.size(); i++) {
            size += headers.get(i).length() + 2;
        }
        size += 2; // CR LF characters
        size += body.length;

        byte[] message = new byte[size];
        int counter = 0;

        for (int i = 0; i < start_line.length; i++) {
            message[counter++] = start_line[i];
        }

        for (int i = 0; i < headers.size(); i++) {
            for (int j = 0; j < headers.get(i).length(); j++) {
                message[counter++] = headers.get(i).getBytes()[j];
            }
            message[counter++] = CR;
            message[counter++] = LF;
        }

        message[counter++] = CR;
        message[counter++] = LF;

        for (int i = 0; i < body.length; i++) {
            message[counter++] = body[i];
        }

        return message;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Lecture de la requête envoyée par le client
                String request = readFirstLine();

                // Analyse requête reçue
                String[] strings = request.split(" "); // Découpage de la requête

                ArrayList<String> headers = new ArrayList<>();

                switch (strings[0]) {
                    case "GET":
                        // On traite la ressource demandée
                        if (strings[1].equals("/")) // On demande index.html
                        {
                            strings[1] = "/index.html";
                        }

                        String[] splitFile = strings[1].split("\\.");
                        String extension = splitFile[splitFile.length - 1];
                        switch (extension) {
                            case "html":
                                headers.add("Content-Type: text/html");
                                break;
                            case "png":
                                headers.add("Content-Type: image/png");
                                break;
                            case "jpg":
                                headers.add("Content-Type: image/jpg");
                                break;
                            case "txt":
                                headers.add("Content-Type: text/plain");
                                break;
                        }
                        break;
                    case "HEAD":
                        break;
                }

                byte[] body = fillBody(strings[1]);

                if (body == null) // Si on a une erreur 404
                {
                    body = reason.getBytes();
                }

                headers.add("Content-Length: " + body.length);

                // Réponse envoyée au client
                byte[] startLine = createResponseStartLine("HTTP/1.1", value, reason);

                byte[] message = createHTTPMessage(startLine, headers, body);
                /*System.out.println();
                System.out.println("REPONSE : ");
                System.out.println(new String(message));*/
                out.write(message);
                out.flush();
                System.out.println(new String(message));
                System.out.println("!!FLUSHED!!");
            } catch (IOException ex) {
                Logger.getLogger(WebConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
