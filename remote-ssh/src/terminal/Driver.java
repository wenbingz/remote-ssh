package terminal;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Driver {
    private final static String relayIP = "localhost";
    private final static int port = 23446;
    private static Socket terminalSocket;
    private static PrintWriter writer;
    private static BufferedReader reader;
    public static void main(String[] args) throws IOException, InterruptedException {
        terminalSocket = new Socket();
        terminalSocket.connect(new InetSocketAddress(relayIP, port));
        writer = new PrintWriter(new OutputStreamWriter(terminalSocket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(terminalSocket.getInputStream()));

        Thread readerThread = new Thread() {
          @Override
          public void run() {
              try {
                  String line = null;
                  while ((line = reader.readLine()) != null) {
                      System.out.println(line);
                  }
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        };
        Thread writerThread = new Thread() {
          @Override
          public void run() {
              BufferedReader keyBoardReader = new BufferedReader(new InputStreamReader(System.in));
              String line = null;
              while (true) {
                  try {
                      if (!((line = keyBoardReader.readLine()) != null)) break;
                      writer.println(line);
                      writer.flush();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }

              }
          }
        };
        readerThread.start();
        writerThread.start();
        readerThread.join();
        writerThread.join();
    }
}
