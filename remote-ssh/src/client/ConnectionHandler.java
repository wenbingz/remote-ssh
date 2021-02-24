package client;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ConnectionHandler extends Thread {
    private Socket socket;
    private String contextPath;
    private BufferedReader reader;
    private PrintWriter writer;
    private final static String prompt = " >>> ";
    private String[] template = {"/bin/sh", "-c", ""};
    //private String[] template = {"CMD", "/C", ""}; // for Windows
    private Map<String, String> envp = new HashMap<>();
    public ConnectionHandler(Socket socket) {
        this.socket = socket;
        this.contextPath = "/";
        try {
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    String changePath(String line) {
        if (line == null || !line.startsWith("cd")) {
            return this.contextPath;
        } else {
            line = line.substring(2).trim();
            if (line.startsWith("/")) {
                return line;
            } else {
                if (this.contextPath.equals("/")) {
                    return this.contextPath + line;
                } else {
                    return this.contextPath + "/" + line;
                }
            }
        }
    }
    @Override
    public void run() {
        writer.print(contextPath + prompt);
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println("received instruction : " + line);
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("cd")) {
                    this.contextPath = changePath(line);
                    writer.println("changed context path to " + this.contextPath);
                    writer.flush();
                } else {
                    template[2] = line;
                    ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(template)).redirectErrorStream(true);
                    System.out.println("prepare");
                    for (Map.Entry<String, String> entry : envp.entrySet()) {
                        processBuilder.environment().put(entry.getKey(), entry.getValue());
                    }
                    processBuilder.directory(new File(contextPath));
                    System.out.println("prepared");
                    Process process = processBuilder.start();
                    System.out.println("started");
                    BufferedReader processReader =
                            new BufferedReader(new InputStreamReader(process.getInputStream()));

                    Thread readThread = new Thread() {
                        @Override
                        public void run() {
                            String outputLine = null;
                            try {
                                while ((outputLine = processReader.readLine()) != null) {
                                    writer.println(outputLine);
                                    writer.flush();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    readThread.start();
                    try {
                        System.out.println("before wait");
                        process.waitFor(10000, TimeUnit.SECONDS);
                        System.out.println("after wait");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                writer.println(contextPath + prompt);
                writer.flush();
        }
    } catch (IOException e) {

        } finally {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
