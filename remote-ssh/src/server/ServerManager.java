package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerManager extends Thread {
    private Map<String, Socket> servers = new HashMap<>();
    private ServerSocket serverSocket;
    public ServerManager(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public Set<String> getServerNames() {
        synchronized (servers) {
            return servers.keySet();
        }
    }
    public boolean informServer(String serverName, int port) {
        synchronized (servers) {
            if (!servers.containsKey(serverName)) {
                return false;
            }
            try {
                servers.get(serverName).getOutputStream().write(("" + port + "\n").getBytes());
                servers.get(serverName).getOutputStream().flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
    @Override
    public void run() {
        while ()
    }

}
