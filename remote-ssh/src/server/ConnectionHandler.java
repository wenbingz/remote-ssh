package server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler extends Thread {
    private Socket clientSocket;
    private ServerSocket relaySocket;
    private Socket serverSocket;
    private PrintWriter clientWriter;
    private PrintWriter serverWriter;
    private BufferedReader serverReader;
    private BufferedReader clientReader;
    public ConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    int allocateNewConn() {
        try {
            relaySocket = new ServerSocket(0, 1, InetAddress.getByAddress(new byte[]{0,0,0,0}));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int port = serverSocket.getLocalPort();
        return port;
    }

    @Override
    public void run() {
        try {
            serverSocket = relaySocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            clientWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            serverWriter = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
            clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            serverReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread readFromServer = new Thread() {
            @Override
            public void run() {
                String line = null;
                try {
                    while ((line = serverReader.readLine()) != null) {
                        clientWriter.println(line);
                        clientWriter.flush();
                    }
                } catch (IOException e) {

                }
            }
        };
        Thread readFromClient = new Thread() {
            @Override
            public void run() {
                String line = null;
                try {
                    while ((line = clientReader.readLine()) != null) {
                        serverWriter.println(line);
                        serverWriter.flush();
                    }
                } catch (IOException e) {

                }
            }
        };
        readFromClient.start();
        readFromServer.start();
        try {
            readFromClient.wait();
            readFromServer.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
