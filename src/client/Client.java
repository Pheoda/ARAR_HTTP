package client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Connection;

public class Client extends Connection{
        
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
            
    public byte[] createRequestStartLine(String s) {
        byte[] message = new byte[s.length() + 4];
        int counter = 0;
        
        for(int i = 0; i < s.length(); i++)
            message[counter++] = s.getBytes()[i];
        
        message[counter++] = CR;
        message[counter++] = LF;
        message[counter++] = CR;
        message[counter++] = LF;
        
        return message;
    }

    @Override
    public void run() {
        try {
            byte[] data = createRequestStartLine("GET /fichier.txt HTTP/1.1");
            out.write(data);
            out.flush();
            
            String response = readBuffer();
            System.out.println(response);
            // On regarde dans reponse la valeur de Content-Length
            String[] lines = response.split("\r\n");
            int i;
            
            for(i = 0; i < lines.length && !lines[i].contains("Content-Length"); i++)
                System.out.println(i + " : " + lines[i]);
            
            if(i == lines.length) {
                System.err.println("Erreur : lines " + i);
                System.exit(1);
            }
            String[] lineContentLength = lines[i].split(": ");
            int length = Integer.parseInt(lineContentLength[1]);
            System.out.println("Length : " + length);
            
            // Demander nom fichier
            String filename = "file.txt";
            
            // Création du fichier
            FileOutputStream file = new FileOutputStream(filename);
            
            // Ecriture dans le fichier            
            for(int j = 0; j < length; j++) {
                file.write(in.read());
            }
            
            file.close();

            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true);
    }
    
}
