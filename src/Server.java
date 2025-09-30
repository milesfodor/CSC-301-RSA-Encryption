import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Server {
    private ServerSocket server;

    public void initAndStart() throws IOException {
        server = new ServerSocket(5000);
        System.out.println("Server is running");
        Socket clientSocket = server.accept();
        System.out.println("Client is connected\n");
        System.out.println("Enter your messages here: ");
        while (true) {
            readMessageFromSocket(clientSocket);
            Scanner kb = new Scanner(System.in);
            String response = kb.nextLine();
            replyToMessage(response, clientSocket);
        }
    }

    private void replyToMessage(String response, Socket clientSocket) throws IOException {
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
        writer.println(response);
    }

    private void readMessageFromSocket(Socket clientSocket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String line = reader.readLine();
        System.out.println("Client: " + line);
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
        } catch (IOException e) {
            server.close();
        }
    }
}