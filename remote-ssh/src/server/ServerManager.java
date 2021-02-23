package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerManager {
    private Map<String, Socket> servers = new HashMap<>();

    public Set<String> getServerNames() {
        synchronized (servers) {
            return servers.keySet();
        }
    }
    public boolean addServer(String serverName, Socket socket) {
        synchronized (servers) {
            servers.put(serverName, socket);
        }
        return  true;
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


}
