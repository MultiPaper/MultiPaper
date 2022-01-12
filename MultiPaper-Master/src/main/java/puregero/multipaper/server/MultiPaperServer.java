package puregero.multipaper.server;

import puregero.multipaper.server.proxy.ProxyServer;

import java.io.IOException;
import java.net.ServerSocket;

public class MultiPaperServer extends Thread {
    public static final int DEFAULT_PORT = 35353;

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Usage: java -jar MultiPaperServer.jar <port> [proxy port]");
                System.exit(1);
            }
        }

        if (args.length > 1) {
            try {
                ProxyServer.openServer(Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                System.err.println("Usage: java -jar MultiPaperServer.jar <port> [proxy port]");
                System.exit(1);
            }
        }

        new MultiPaperServer(port).start();

        new CommandLineInput().run();
    }

    private ServerSocket serverSocket;

    public MultiPaperServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        System.out.println("[MultiPaperServer] Listening on " + serverSocket.getLocalSocketAddress());
    }

    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                new ServerConnection(serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
