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


    public void init() throws Exception {
        socket = new Socket("localhost", 5000);
        System.out.println("Connecting to the server");

        keyPair = RSA.generateKeyPair();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String serverKey = reader.readLine();
        serverPublicKey = RSA.stringToKey(serverKey);
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(RSA.keyToString(keyPair.getPublic()));
    }

    public void sendMessage(String msg) throws Exception {
        String encryptedMessage = RSA.encrypt(msg, serverPublicKey);
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(encryptedMessage);
    }

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
            while (true) {
                String msg = kb.nextLine();
                client.sendMessage(msg);
                client.readResponseFromServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readResponseFromServer() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response = reader.readLine();
        System.out.println("Encrypted Message: " + response);
        String decryptedResponse = RSA.decrypt(response, keyPair.getPrivate());
        System.out.println("Server: " + decryptedResponse);
    }
}