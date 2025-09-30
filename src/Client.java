import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;

    public void init() throws IOException {
        socket = new Socket("localhost", 5000);
        System.out.println("Connecting to the server");
    }

    public void sendMessage(String msg) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(msg);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readResponseFromServer() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response = reader.readLine();
        System.out.println("Server: " + response);
    }
}