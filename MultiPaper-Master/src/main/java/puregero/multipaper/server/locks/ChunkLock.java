package puregero.multipaper.server.locks;

import puregero.multipaper.server.ChunkKey;
import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkLock {

    private static final Queue<List<ServerConnection>> objectPool = new LinkedList<>();

    // Index 0 in the list is the current chunk owning it
    private static final Map<ChunkKey, List<ServerConnection>> chunkSubscribers = new ConcurrentHashMap<>();

    private static final Map<ServerConnection, HashSet<ChunkKey>> lockedChunks = new HashMap<>();

    public static ServerConnection getOwner(String world, int cx, int cz) {
        synchronized (chunkSubscribers) {
            List<ServerConnection> serverConnections = chunkSubscribers.get(new ChunkKey(world, cx, cz));
            if (serverConnections != null && !serverConnections.isEmpty()) {
                return serverConnections.get(0);
            }
        }
        return null;
    }

    public static ServerConnection lock(ServerConnection serverConnection, String world, int cx, int cz) {
        ChunkKey key = new ChunkKey(world, cx, cz);
        synchronized (chunkSubscribers) {
            List<ServerConnection> serverConnections = chunkSubscribers.computeIfAbsent(key, key2 -> {
                synchronized (objectPool) {
                    List<ServerConnection> list = objectPool.poll();
                    if (list != null) {
                        return list;
                    }
                }
                return new ArrayList<>();
            });

            if (!serverConnections.contains(serverConnection)) {
                serverConnections.add(serverConnection);
                lockedChunks.computeIfAbsent(serverConnection, k -> new HashSet<>()).add(key);
            }

            return serverConnections.get(0);
        }
    }

    public static void unlock(ServerConnection serverConnection, String world, int cx, int cz) {
        unlock(serverConnection, new ChunkKey(world, cx, cz));
    }

    public static void unlock(ServerConnection serverConnection, ChunkKey key) {
        synchronized (chunkSubscribers) {
            List<ServerConnection> serverConnections = chunkSubscribers.get(key);
            if (serverConnections != null) {
                for (int i = 0; i < serverConnections.size(); i++) {
                    if (serverConnections.get(i) == serverConnection) {
                        serverConnections.remove(i--);

                        if (i == -1 && !serverConnections.isEmpty()) {
                            updateOwner(serverConnections, key.name, key.x, key.z);
                        }
                    }
                }
                if (serverConnections.isEmpty()) {
                    chunkSubscribers.remove(key);
                    objectPool.add(serverConnections);
                }
            }

            HashSet<ChunkKey> chunks = lockedChunks.get(serverConnection);
            if (chunks != null) {
                chunks.remove(key);
            }
        }
    }

    private static void updateOwner(List<ServerConnection> serverConnections, String world, int cx, int cz) {
        String owner = serverConnections.get(0).getBungeeCordName();
        for (ServerConnection connection : serverConnections) {
            CompletableFuture.runAsync(() -> {
                try {
                    DataOutputSender out = connection.buffer();
                    out.writeUTF("chunkOwner");
                    out.writeUTF(world);
                    out.writeInt(cx);
                    out.writeInt(cz);
                    out.writeUTF(owner);
                    out.send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void unlockAll(ServerConnection serverConnection) {
        synchronized (chunkSubscribers) {
            HashSet<ChunkKey> chunks = lockedChunks.remove(serverConnection);
            if (chunks != null) {
                chunks.forEach(chunk -> unlock(serverConnection, chunk));
            }
        }
    }
}
