package puregero.multipaper.server;

import puregero.multipaper.server.handlers.Handler;
import puregero.multipaper.server.handlers.Handlers;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ServerConnection extends Thread {
    private final Socket socket;

    private String name;
    private long lastPing = System.currentTimeMillis();
    private final CircularTimer timer = new CircularTimer();
    private final Map<Integer, Consumer<DataInputStream>> callbacks = new ConcurrentHashMap<>();
    private final List<Player> players = new ArrayList<>();
    private double tps;
    private int port = -1;
    private String host;

    /**
     * This connection map may include dead servers! Check if a server is alive
     * with `connections` before trying to send any data!
     */
    private static final Map<String, ServerConnection> connectionMap = new ConcurrentHashMap<>();

    private static final List<ServerConnection> connections = new ArrayList<>();

    public static void shutdown() {
        try {
            DataOutputStream broadcast = broadcastAll();
            broadcast.writeInt(-1);
            broadcast.writeUTF("shutdown");
            broadcast.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void shutdownAndWait() {
        while (!connections.isEmpty()) {
            shutdown();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ServerConnection(Socket socket) {
        this.socket = socket;
        setName("ServerConnection-" + socket.getRemoteSocketAddress());

        start();
    }

    public static boolean isAlive(String bungeecordName) {
        return connections.contains(getConnection(bungeecordName)) && getConnection(bungeecordName).isOnline();
    }

    public static ServerConnection getConnection(String bungeecordName) {
        return connectionMap.get(bungeecordName);
    }

    public static List<ServerConnection> getConnections() {
        return connections;
    }

    public void send(byte[] bytes) throws IOException {
        if (!socket.isClosed()) {
            synchronized (socket) {
                socket.getOutputStream().write(bytes);
                socket.getOutputStream().flush();
            }
        }
    }

    public void send(byte[] bytes, int id, Consumer<DataInputStream> callback) throws IOException {
        if (!socket.isClosed()) {
            synchronized (socket) {
                socket.getOutputStream().write(bytes);
                socket.getOutputStream().flush();
            }
        }
        callbacks.put(id, callback);
    }

    public DataOutputSender buffer() throws IOException {
        return new DataOutputSender(this);
    }

    public DataOutputSender buffer(int id) throws IOException {
        return new DataOutputSender(this, id);
    }

    public static DataOutputStream broadcastAll() {
        return new DataOutputStream(new ByteArrayOutputStream() {
            @Override
            public void close() {
                connections.forEach(connection -> {
                    try {
                        connection.send(toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public DataOutputStream broadcastOthers() {
        return new DataOutputStream(new ByteArrayOutputStream() {
            @Override
            public void close() {
                connections.forEach(connection -> {
                    if (connection != ServerConnection.this) {
                        try {
                            connection.send(toByteArray());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public boolean isOnline() {
        return lastPing > System.currentTimeMillis() - 5000 && tps > 0;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            name = in.readUTF();
            host = ((InetSocketAddress) getAddress()).getAddress().getHostAddress();

            synchronized (connections) {
                connections.add(this);
                connectionMap.put(name, this);
            }

            System.out.println("Connection from " + socket.getRemoteSocketAddress() + " (" + name + ")");

            sendSecret();
            
            while (!socket.isClosed()) {
                int id = in.readInt();
                String command = in.readUTF();

                lastPing = System.currentTimeMillis();

                Consumer<DataInputStream> callback = callbacks.remove(id);

                if (callback != null) {
                    callback.accept(in);
                    continue;
                }

                Handler handler = Handlers.get(command);

                if (handler == null) {
                    System.err.println(command + " has no Handler in Handlers!");
                }

                handler.handle(this, in, buffer(id));
            }
        } catch (EOFException e) {
            // Ignored
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (Exception ignored) {}

        EntitiesSubscriptionManager.unsubscribeAll(this);
        ChunkSubscriptionManager.unsubscribeAndUnlockAll(this);

        synchronized (connections) {
            connections.remove(this);
        }

        System.out.println(socket.getRemoteSocketAddress() + " (" + name + ") closed");
    }

    private void sendSecret() {
        try {
            DataOutputSender out = buffer();
            out.writeUTF("secret");
            out.writeUTF(MultiPaperServer.SECRET);
            out.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBungeeCordName() {
        return name;
    }

    public CircularTimer getTimer() {
        return timer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        if (this.tps == -1) {
            throw new IllegalStateException("Trying to set " + getBungeeCordName() + "'s tps to " + tps + " when it is marked as offline (" + this.tps + " tps)");
        }
        
        this.tps = tps;
    }

    public SocketAddress getAddress() {
        return socket.getRemoteSocketAddress();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
