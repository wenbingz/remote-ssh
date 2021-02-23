package client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Driver {

    private final static String relayIP = "localhost";
    private final static int port = 23445;
    private final static String identifier = "guiyang thinkpad";
    private static BufferedReader reader = null;
    private static PrintWriter writer = null;
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        System.out.println("try to connect");
        socket.connect(new InetSocketAddress(relayIP, port));
        System.out.println("connection done");
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
                e.printStackTrace();
            }
            System.out.println("read a new port [" + port + "]");
            Socket newConn = new Socket();
            newConn.connect(new InetSocketAddress(relayIP, port));
            System.out.println("connected to [" + relayIP + ", " + port + "]");
            new ConnectionHandler(newConn).start();
        }
    }
}
