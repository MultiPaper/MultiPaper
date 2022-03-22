package puregero.multipaper.server;

import org.json.JSONObject;
import puregero.multipaper.server.proxy.ProxyServer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MultiPaperServer extends Thread {
    public static final int DEFAULT_PORT = 35353;
    public static final String SECRET = UUID.randomUUID().toString();

    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getByName("0.0.0.0");
        int port = DEFAULT_PORT;

        if (args.length > 0) {
            try {
                if (args[0].contains(":")) {
                    String[] split = args[0].split(":", 2);
                    address = InetAddress.getByName(split[0]);
                    port = Integer.parseInt(split[1]);
                } else {
                    port = Integer.parseInt(args[0]);
                }
            } catch (NumberFormatException e) {
                System.err.println("Usage: java -jar MultiPaperServer.jar <address>:<port> [proxy port]");
                System.exit(1);
            }
        }

        if (args.length > 1) {
            try {
                ProxyServer.openServer(Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                System.err.println("Usage: java -jar MultiPaperServer.jar <address>:<port> [proxy port]");
                System.exit(1);
            }
        }

        new MultiPaperServer(address, port).start();

        new CommandLineInput().run();
    }

    private final ServerSocket serverSocket;

    public MultiPaperServer(InetAddress address, int port) throws IOException {
        serverSocket = new ServerSocket(port, 0, address);

        System.out.println("[MultiPaperMaster] Listening on " + serverSocket.getLocalSocketAddress());
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
