package puregero.multipaper.server;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class EntitiesSubscriptionManager {

    private static final Queue<List<ServerConnection>> objectPool = new LinkedList<>();

    private static final Map<ChunkKey, List<ServerConnection>> chunkSubscribers = new ConcurrentHashMap<>();
    private static final Map<ServerConnection, HashSet<ChunkKey>> subscribedChunks = new HashMap<>();

    public static ServerConnection getSubscriber(String world, int cx, int cz) {
        synchronized (chunkSubscribers) {
            List<ServerConnection> serverConnections = chunkSubscribers.get(new ChunkKey(world, cx, cz));
            if (serverConnections != null && !serverConnections.isEmpty()) {
                return serverConnections.get(0);
            }
        }

        return null;
    }

    public static void subscribe(ServerConnection serverConnection, String world, int cx, int cz) {
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
                if (!serverConnections.isEmpty()) {
                    List<ServerConnection> singletonList = Collections.singletonList(serverConnection);
                    for (ServerConnection subscriber : serverConnections) {
                        updateSubscriberAdd(singletonList, subscriber, world, cx, cz);
                    }
                }

                serverConnections.add(serverConnection);
                updateSubscriberAdd(serverConnections, serverConnection, world, cx, cz);
                subscribedChunks.computeIfAbsent(serverConnection, k -> new HashSet<>()).add(key);
            }
        }
    }

    public static void unsubscribe(ServerConnection serverConnection, String world, int cx, int cz) {
        unsubscribe(serverConnection, new ChunkKey(world, cx, cz));
    }

    public static void unsubscribe(ServerConnection serverConnection, ChunkKey key) {
        synchronized (chunkSubscribers) {
            List<ServerConnection> serverConnections = chunkSubscribers.get(key);
            if (serverConnections != null) {
                if (serverConnections.remove(serverConnection)) {
                    updateSubscriberRemoved(serverConnections, serverConnection, key.name, key.x, key.z);
                }
                if (serverConnections.isEmpty()) {
                    chunkSubscribers.remove(key);
                    synchronized (objectPool) {
                        objectPool.add(serverConnections);
                    }
                }
            }

            HashSet<ChunkKey> chunks = subscribedChunks.get(serverConnection);
            if (chunks != null) {
                chunks.remove(key);
            }
        }
    }

    private static void updateSubscriberAdd(List<ServerConnection> serverConnections, ServerConnection serverConnection, String world, int cx, int cz) {
        String subscriber = serverConnection.getBungeeCordName();
        for (ServerConnection connection : serverConnections) {
            if (connection != serverConnection) {
                CompletableFuture.runAsync(() -> {
                    try {
                        DataOutputSender out = connection.buffer();
                        out.writeUTF("entitiesSubscribe");
                        out.writeUTF(world);
                        out.writeInt(cx);
                        out.writeInt(cz);
                        out.writeUTF(subscriber);
                        out.send();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private static void updateSubscriberRemoved(List<ServerConnection> serverConnections, ServerConnection serverConnection, String world, int cx, int cz) {
        String unsubscriber = serverConnection.getBungeeCordName();
        for (ServerConnection connection : serverConnections) {
            CompletableFuture.runAsync(() -> {
                try {
                    DataOutputSender out = connection.buffer();
                    out.writeUTF("entitiesUnsubscribe");
                    out.writeUTF(world);
                    out.writeInt(cx);
                    out.writeInt(cz);
                    out.writeUTF(unsubscriber);
                    out.send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void unsubscribeAll(ServerConnection serverConnection) {
        synchronized (chunkSubscribers) {
            HashSet<ChunkKey> chunks = subscribedChunks.remove(serverConnection);
            if (chunks != null) {
                chunks.forEach(chunk -> unsubscribe(serverConnection, chunk));
            }
        }
    }
}
