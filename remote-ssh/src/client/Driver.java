package client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Driver {
    // test commit 1
    // test commit 2
    // test commit 3
    private final static String relayIP = "wenbing.space";
    private final static int port = 23445;
    private final static String identifier = "my macbook";
    private final static long retrySeconds = 5;
    private static BufferedReader reader = null;
    private static PrintWriter writer = null;
    public static void main(String[] args) {
        while (true) {
            Socket socket = new Socket();
            System.out.println("try to connect");
            try {
                socket.connect(new InetSocketAddress(relayIP, port));
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                continue;
            }
            System.out.println("connection done");
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            writer.println(identifier);
            writer.flush();
            String line = null;
            while (true) {
                try {
                    if (!((line = reader.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                line = line.trim();
                int port = 0;
                try {
                    port = Integer.parseInt(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("read a new port [" + port + "]");
                Socket newConn = new Socket();
                try {
                    newConn.connect(new InetSocketAddress(relayIP, port));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("connected to [" + relayIP + ", " + port + "]");
                new ConnectionHandler(newConn).start();
            }
            System.out.println("retry after " + retrySeconds + " seconds");
            try {
                Thread.sleep(retrySeconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
