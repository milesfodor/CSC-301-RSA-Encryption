import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Scanner;


public class Server {
    private ServerSocket server;
    private KeyPair keyPair;
    private PublicKey clientPublicKey;

    //initialization
    public void initAndStart() throws Exception {
        keyPair = RSA.generateKeyPair();    //generate the key pair

        server = new ServerSocket(5000);    //creates a socket and connects it to the port
        System.out.println("Server is running");
        Socket clientSocket = server.accept();  //accept the client to the server
        System.out.println("Client is connected\n");

        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
        writer.println(RSA.keyToString(keyPair.getPublic()));   //send the public key to the client
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String clientKey = reader.readLine();   //receive the client's public key as a string
        clientPublicKey = RSA.stringToKey(clientKey);   //convert the string to a key

        System.out.println("Enter your messages here: ");
        //the messaging loop that continues until either the server or client closes the socket
        while (true) {
            readMessageFromSocket(clientSocket);
            Scanner kb = new Scanner(System.in);
            String response = kb.nextLine();
            replyToMessage(response, clientSocket);
        }
    }

    private void replyToMessage(String response, Socket clientSocket) throws Exception {
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
        String encryptedMessage = RSA.encrypt(response, clientPublicKey);   //encrypts the message
        writer.println(encryptedMessage);   //sends the message
    }

    private void readMessageFromSocket(Socket clientSocket) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String line = reader.readLine();    //receive the message from the client
        System.out.println("Encrypted Message: " + line);   //display the encrypted message
        String decryptedMessage = RSA.decrypt(line, keyPair.getPrivate());  //decrypt the message using the private key
        System.out.println("Client: " + decryptedMessage);  //display the decrypted message
    }

    //close the socket to end communications
    public void close() {
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();

        try {
            server.initAndStart();
        } catch (Exception e) {
            server.close();
        }
    }
}
