package server;

import java.io.*;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Driver {
    private static final int clientPort = 23445;
    private static final int terminalPort = 23446;
    private static ServerSocket clientServerSocket;
    private static ServerSocket terminalServerSocket;
    private static ServerManager serverManager;
    public static void main(String[] args) throws IOException, InterruptedException {
        clientServerSocket = new ServerSocket();
        clientServerSocket.bind(new InetSocketAddress("0.0.0.0", clientPort));
        terminalServerSocket = new ServerSocket();
        terminalServerSocket.bind(new InetSocketAddress("0.0.0.0", terminalPort));
        serverManager = new ServerManager();
        Thread clientThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("client thread : try to accept a new client connection");
                        Socket clientSocket = clientServerSocket.accept();
                        System.out.println("client thread : got one");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String serverName = null;
                        serverName = reader.readLine();
                        System.out.println("client thread : and its name is " + serverName);
                        if (serverName != null) {
                            serverManager.addServer(serverName, clientSocket);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        Thread terminalThread = new Thread() {
          @Override
          public void run() {
              while (true) {
                  try {
                      System.out.println("terminal thread : try to get new connection");
                      Socket terminalSocket = terminalServerSocket.accept();
                      System.out.println("terminal thread : got one");
                      ConnectionHandler handler = new ConnectionHandler(terminalSocket, serverManager);
                      handler.start();
                      handler.useItAfterThreadStarted();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
          }
        };
        clientThread.start();
        terminalThread.start();
        clientThread.join();
        terminalThread.join();
    }
}
