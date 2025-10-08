package testservers;

import java.io.*;
import java.net.Socket;

import java.io.*;
import java.net.Socket;

public class DummyClient {
    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        int port = 8080;

        Socket socket = new Socket(host, port);

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

            out.write(input);
            out.newLine();
            out.flush();

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
