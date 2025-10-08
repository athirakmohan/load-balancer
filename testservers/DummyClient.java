package testservers;

import java.io.*;
import java.net.Socket;

import java.io.*;
import java.net.Socket;

public class DummyClient {
    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1"; // load balancer
        int port = 8080;           // LB listening port

        Socket socket = new Socket(host, port);

        // Streams for sending/receiving data
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        String input;
        while (true) {
            System.out.print("> ");
            input = console.readLine();

            if (input == null || "exit".equalsIgnoreCase(input.trim())) {
                break;
            }

            // Send message to load balancer
            out.write(input);
            out.newLine();  // important for readLine() on backend
            out.flush();

            // Read response from backend
            String response = in.readLine();
            if (response == null) {
                System.out.println("(Server closed connection)");
                break;
            }
            System.out.println("Response: " + response);
        }

        socket.close();
    }
}
