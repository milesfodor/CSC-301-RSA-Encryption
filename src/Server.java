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

    public void initAndStart() throws Exception {
        keyPair = RSA.generateKeyPair();

        server = new ServerSocket(5000);
        System.out.println("Server is running");
        Socket clientSocket = server.accept();
        System.out.println("Client is connected\n");

        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
        writer.println(RSA.keyToString(keyPair.getPublic()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String clientKey = reader.readLine();
        clientPublicKey = RSA.stringToKey(clientKey);

        System.out.println("Enter your messages here: ");
        while (true) {
            readMessageFromSocket(clientSocket);
            Scanner kb = new Scanner(System.in);
            String response = kb.nextLine();
            replyToMessage(response, clientSocket);
        }
    }

    private void replyToMessage(String response, Socket clientSocket) throws Exception {
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
        String encryptedMessage = RSA.encrypt(response, clientPublicKey);
        writer.println(encryptedMessage);
    }

    private void readMessageFromSocket(Socket clientSocket) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String line = reader.readLine();
        System.out.println("Encrypted Message: " + line);
        String decryptedMessage = RSA.decrypt(line, keyPair.getPrivate());
        System.out.println("Client: " + decryptedMessage);
    }

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