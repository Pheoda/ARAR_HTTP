package client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Connection;

public class Client extends Connection {

    public Client(InetAddress ia, int port) throws IOException {
        super(new Socket(ia, port));
    }

    public static void main(String[] args) {
        try {
            InetAddress ad = InetAddress.getByName("127.0.0.1");
            Client c = new Client(ad, 2000);
            new Thread(c).start();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public byte[] createRequestStartLine(String type, String file, String version) {
        byte[] message = new byte[type.length() + 1 + file.length() + 1 + version.length() + 4];
        int counter = 0;

        for (int i = 0; i < type.length(); i++) {
            message[counter++] = type.getBytes()[i];
        }

        message[counter++] = ' ';

        for (int i = 0; i < file.length(); i++) {
            message[counter++] = file.getBytes()[i];
        }

        message[counter++] = ' ';

        for (int i = 0; i < version.length(); i++) {
            message[counter++] = version.getBytes()[i];
        }

        message[counter++] = CR;
        message[counter++] = LF;
        message[counter++] = CR;
        message[counter++] = LF;

        return message;
    }

    @Override
    public void run() {
        try {
            String filename = "/data/erreur.txt";
            byte[] data = createRequestStartLine("GET", filename, "HTTP/1.1");
            out.write(data);
            out.flush();

            String response = readBuffer();
            System.out.println(response);
            // On regarde dans reponse la valeur de Content-Length
            String[] lines = response.split("\r\n");
            int i;
            int length = 0;

            // Test de réussite 
            String[] firstLine = lines[0].split(" ");
            int returnCode = Integer.parseInt(firstLine[1]);

            // En cas de retour de valeur incorrect, on prévient l'utilisateur de l'erreur
            if (returnCode != 200) {
                System.err.println("ERREUR " + returnCode);
            }

            for (i = 0; i < lines.length && !lines[i].contains("Content-Length"); i++);

            if (i != lines.length) {
                length = Integer.parseInt(lines[i].split(": ")[1]);
            } else {
                System.err.println("Erreur : Content-Length non trouvé");
                System.exit(1);
            }

            // En cas de déroulement classique du transfert
            if (returnCode == 200) {
                // Demander nom fichier
                String[] str = filename.split("\\.");
                String extension = "";
                if (str.length > 0) {
                    extension = str[str.length - 1];
                }
                String localName = "file" + "." + extension;

                // Création du fichier
                FileOutputStream file = new FileOutputStream(localName);

                // Ecriture dans le fichier
                for (int j = 0; j < length; j++) {
                    file.write(bufIn.read());
                }

                file.close();

                System.out.println("Chargement terminé avec succès");
            } else { // En cas de code retour d'erreur
                for (int j = 0; j < length; j++) {
                    System.err.print((char) bufIn.read());
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true);
    }

}
