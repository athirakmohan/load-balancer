package testservers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DummyBackendServer {

    private int port;

    public DummyBackendServer(int port) {
        this.port = port;
    }

    public void start() {
        ServerSocket serverSocket = null;
        System.out.println("Starting dummy backend service on port " + port + "...");

        try {
            // Start listening on the specified port
            serverSocket = new ServerSocket(port);
            System.out.println("Service ready. Listening for connections...");

            // Loop forever, accepting client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();

                // Handle the connection in a separate thread (optional, but good practice)
                new Thread(new ConnectionHandler(clientSocket, port)).start();
            }

        } catch (IOException e) {
            System.err.println("Could not listen on port " + port + ": " + e.getMessage());
        } finally {
            if (serverSocket != null) {
                try { serverSocket.close(); } catch (IOException e) {}
            }
        }
    }

    // Inner class to handle individual client connections
    private static class ConnectionHandler implements Runnable {
        private Socket clientSocket;
        private int port;

        public ConnectionHandler(Socket socket, int port) {
            this.clientSocket = socket;
            this.port = port;
        }

        public void run() {
            try {
                System.out.println("Port " + port + ": Client connected from " + clientSocket.getInetAddress().getHostAddress());

                OutputStream out = clientSocket.getOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Port " + port + ": Received: " + line);

                    // Send a response back
                    String response = "HTTP/1.1 200 OK\r\nContent-Length: 20\r\n\r\nHello from port " + port;
                    out.write(response.getBytes());
                    out.flush();  // important for immediate delivery
                }

                System.out.println("Port " + port + ": Client disconnected.");

            } catch (IOException e) {
                System.err.println("Port " + port + ": I/O error: " + e.getMessage());
            } finally {
                try { clientSocket.close(); } catch (IOException e) {}
            }
        }

    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:  java -cp testservers DummyBackendServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        new DummyBackendServer(port).start();
    }
}