package client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Driver {

    private final static String relayIP = "wenbing.space";
    private final static int port = 23456;
    private final static String identifier = "guiyang thinkpad";
    private static BufferedReader reader = null;
    private static PrintWriter writer = null;
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(relayIP, port));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.println(identifier);
        writer.flush();
        String line = null;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            int port = 0;
            try {
                port = Integer.parseInt(line);
            } catch (Exception e) {

            }
            Socket newConn = new Socket();
            socket.connect(new InetSocketAddress(relayIP, port));
            new ConnectionHandler(socket).start();
        }
    }
}
