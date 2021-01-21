package puregero.multipaper.server;

import puregero.multipaper.server.handlers.Handler;
import puregero.multipaper.server.handlers.Handlers;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class ServerConnection extends Thread {
    private final Socket socket;

    private String name;
    private long lastPing = System.currentTimeMillis();
    private CircularTimer timer = new CircularTimer();
    private HashMap<Integer, Consumer<DataInputStream>> callbacks = new HashMap<>();
    private List<Player> players = new ArrayList<>();
    private double tps;

    public HashSet<String> loadedChunks = new HashSet<>();

    /**
     * This connection map may include dead servers! Check if a server is alive
     * with `connections` before trying to send any data!
     */
    private static HashMap<String, ServerConnection> connectionMap = new HashMap<>();

    private static List<ServerConnection> connections = new ArrayList<>();

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
        synchronized (socket) {
            socket.getOutputStream().write(bytes);
        }
    }

    public void send(byte[] bytes, int id, Consumer<DataInputStream> callback) throws IOException {
        synchronized (socket) {
            socket.getOutputStream().write(bytes);
            callbacks.put(id, callback);
        }
    }

    public DataOutputSender buffer() throws IOException {
        return new DataOutputSender(this);
    }

    public DataOutputSender buffer(int id) throws IOException {
        return new DataOutputSender(this, id);
    }

    public DataOutputStream broadcastAll() {
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

            connections.add(this);
            connectionMap.put(name, this);

            System.out.println("Connection from " + socket.getRemoteSocketAddress() + " (" + name + ")");

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

        connections.remove(this);
        System.out.println(socket.getRemoteSocketAddress() + " (" + name + ") closed");
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
}
