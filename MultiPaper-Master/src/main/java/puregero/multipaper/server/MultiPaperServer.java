package puregero.multipaper.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class MultiPaperServer extends Thread {
    public static final int DEFAULT_PORT = 35353;
    private static final int WORKER_COUNT = 64;

    private final ArrayList<Worker> workers = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Usage: java -jar MultiPaperServer.jar <port>");
                System.exit(1);
            }
        }

        new MultiPaperServer(port).run();
    }

    private ServerSocket serverSocket;

    public MultiPaperServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        System.out.println("[MultiPaperServer] Listening on " + serverSocket.getLocalSocketAddress());

        for (int i = 0; i < WORKER_COUNT; i++) {
            workers.add(new Worker());
        }
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

        workers.forEach(Thread::interrupt);
    }
}
