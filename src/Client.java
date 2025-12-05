import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private KeyPair keyPair;
    private PublicKey serverPublicKey;


    //initialization
    public void init() throws Exception {
        socket = new Socket("PI_IP", 5000); //creates a socket and connects it to the port
        System.out.println("Connecting to the server");

        keyPair = RSA.generateKeyPair();    //generate the key pair
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String serverKey = reader.readLine();   //receive the server's public key as a string
        serverPublicKey = RSA.stringToKey(serverKey);   //convert the string to a key
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(RSA.keyToString(keyPair.getPublic()));   //send public key to the server
    }

    //send messages
    public void sendMessage(String msg) throws Exception {
        String encryptedMessage = RSA.encrypt(msg, serverPublicKey);    //encrypt the message using the server's public key
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(encryptedMessage);   //send message
    }

    //close the socket to end communications
    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();

        try {
            client.init();
            Scanner kb = new Scanner(System.in);
            System.out.println("Connected!\n");
            System.out.println("Enter your messages here: ");
            //messaging loop
            while (true) {
                String msg = kb.nextLine();
                client.sendMessage(msg);
                client.readResponseFromServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //read the messages from the server
    private void readResponseFromServer() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response = reader.readLine();    //receive the message
        System.out.println("Encrypted Message: " + response);   //display the encrypted message
        String decryptedResponse = RSA.decrypt(response, keyPair.getPrivate()); //decrypt the message using the private key
        System.out.println("Server: " + decryptedResponse); //display the decrypted message
    }
}
