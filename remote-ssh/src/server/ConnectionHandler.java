package server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler extends Thread {
    private Socket terminalSocket;
    private ServerSocket relaySocket;
    private Socket serverSocket;
    private PrintWriter clientWriter;
    private PrintWriter serverWriter;
    private BufferedReader serverReader;
    private BufferedReader clientReader;
    private ServerManager serverManager;
    private int port = -1;
    private String serverName = null;
    public ConnectionHandler(Socket terminalSocket, ServerManager serverManager) {
        this.terminalSocket = terminalSocket;
        this.serverManager = serverManager;
        try {
            allocateNewConn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void allocateNewConn() throws IOException {

        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(terminalSocket.getOutputStream()));
        System.out.println("connection handler : output current server list " + serverManager.getServerNames());
        printWriter.println(serverManager.getServerNames());
        printWriter.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(terminalSocket.getInputStream()));
        String serverName = null;
        serverName = reader.readLine();
        System.out.println("connection handler : it want to build a session with [" + serverName + "]");
        this.serverName = serverName;
        try {
            relaySocket = new ServerSocket(0, 1, InetAddress.getByAddress(new byte[]{0,0,0,0}));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int port = relaySocket.getLocalPort();
        System.out.println("connection handler : allocate port " + port + " for this seesion");
        this.port = port;
    }

    public void useItAfterThreadStarted() {
        this.serverManager.informServer(serverName, port);
    }


    @Override
    public void run() {
        try {
            serverSocket = relaySocket.accept();
            System.out.println("connection handler : client connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            clientWriter = new PrintWriter(new OutputStreamWriter(terminalSocket.getOutputStream()));
            serverWriter = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
            clientReader = new BufferedReader(new InputStreamReader(terminalSocket.getInputStream()));
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
            readFromClient.join();
            readFromServer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
