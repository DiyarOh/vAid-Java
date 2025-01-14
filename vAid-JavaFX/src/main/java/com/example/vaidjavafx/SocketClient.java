package com.example.vaidjavafx;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    private static final String SERVER_IP = "192.168.8.10";
    private static final int SERVER_PORT = 5001;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to the server.");

            // Example: Send a PING command
            out.println("PING");
            String response = in.readLine();
            System.out.println("Response from server: " + response);

            // Example: Send a TRIGGER command
            out.println("TRIGGER");
            response = in.readLine();
            System.out.println("Response from server: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}