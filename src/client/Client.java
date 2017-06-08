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
            
    public byte[] createRequestStartLine(String type, String file, String version) {
        byte[] message = new byte[type.length() + 1 + file.length() + 1 + version.length() + 4];
        int counter = 0;
        
        for(int i = 0; i < type.length(); i++)
            message[counter++] = type.getBytes()[i];
        
        message[counter++] = ' ';
        
        for(int i = 0; i < file.length(); i++)
            message[counter++] = file.getBytes()[i];
        
        message[counter++] = ' ';
        
        for(int i = 0; i < version.length(); i++)
            message[counter++] = version.getBytes()[i];
        
        message[counter++] = CR;
        message[counter++] = LF;
        message[counter++] = CR;
        message[counter++] = LF;
        
        return message;
    }

    @Override
    public void run() {
        try {
            String filename = "/fichier.txt";
            byte[] data = createRequestStartLine("GET", filename, "HTTP/1.1");
            out.write(data);
            out.flush();
            
            String response = readBuffer();
            System.out.println(response);
            // On regarde dans reponse la valeur de Content-Length
            String[] lines = response.split("\r\n");
            int i;
            int length = 0;
            
            for(i = 0; i < lines.length; i++) {
                if(lines[i].contains("Content-Length")) {
                    length = Integer.parseInt(lines[i].split(": ")[1]);
                    System.out.println("Length : " + length);
                }
            }
            
            // Demander nom fichier
            String[] str = filename.split("\\.");
            String extension = "";
            if(str.length > 0)
                extension = str[str.length - 1];
            String localName = "file" + "." + extension;
            
            // Création du fichier
            FileOutputStream file = new FileOutputStream(localName);
            
            // Ecriture dans le fichier            
            for(int j = 0; j < length; j++) {
                
            }
            
            file.close();

            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true);
    }
    
}
