package puregero.multipaper.database;

import puregero.multipaper.databasemessagingprotocol.ChunkKey;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.AddEntitySubscriberMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.RemoveEntitySubscriberMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.EntitySubscribersSyncMessage;
import puregero.multipaper.database.util.EntitiesLock;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntitiesSubscriptionManager {

    private static final Queue<List<ServerConnection>> objectPool = new LinkedList<>();

    private static final Map<ChunkKey, List<ServerConnection>> chunkSubscribers = new ConcurrentHashMap<>();
    private static final Map<ServerConnection, HashSet<ChunkKey>> subscribedChunks = new HashMap<>();

    public static ServerConnection getSubscriber(String world, int cx, int cz) {
        ChunkKey key = new ChunkKey(world, cx, cz);
        synchronized (EntitiesLock.getEntitiesLock(key)) {
            List<ServerConnection> serverConnections = chunkSubscribers.get(key);
            if (serverConnections != null && !serverConnections.isEmpty()) {
                return serverConnections.get(0);
            }
        }

        return null;
    }

    public static void subscribe(ServerConnection serverConnection, String world, int cx, int cz) {
        ChunkKey key = new ChunkKey(world, cx, cz);
        synchronized (EntitiesLock.getEntitiesLock(key)) {
            List<ServerConnection> serverConnections = chunkSubscribers.computeIfAbsent(key, key2 -> {
                synchronized (objectPool) {
                    List<ServerConnection> list = objectPool.poll();
                    if (list != null) {
                        return list;
                    }
                }
                return new ArrayList<>();
            });

            if (!serverConnections.isEmpty()) {
                List<ServerConnection> singletonList = Collections.singletonList(serverConnection);
                for (ServerConnection subscriber : serverConnections) {
                    if (subscriber != serverConnection) {
                        updateSubscriberAdd(singletonList, subscriber, world, cx, cz);
                    }
                }
            }

            if (!serverConnections.contains(serverConnection)) {
                updateSubscriberAdd(serverConnections, serverConnection, world, cx, cz);
                serverConnections.add(serverConnection);
                subscribedChunks.computeIfAbsent(serverConnection, k -> new HashSet<>()).add(key);
            }
        }
    }

    public static void unsubscribe(ServerConnection serverConnection, String world, int cx, int cz) {
        unsubscribe(serverConnection, new ChunkKey(world, cx, cz));
    }

    public static void unsubscribe(ServerConnection serverConnection, ChunkKey key) {
        synchronized (EntitiesLock.getEntitiesLock(key)) {
            List<ServerConnection> serverConnections = chunkSubscribers.get(key);
            if (serverConnections != null) {
                if (serverConnections.remove(serverConnection)) {
                    updateSubscriberRemoved(serverConnections, serverConnection, key.world, key.x, key.z);
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
                connection.send(new AddEntitySubscriberMessage(world, cx, cz, subscriber));
            }
        }
    }

    private static void updateSubscriberRemoved(List<ServerConnection> serverConnections, ServerConnection serverConnection, String world, int cx, int cz) {
        String unsubscriber = serverConnection.getBungeeCordName();
        for (ServerConnection connection : serverConnections) {
            connection.send(new RemoveEntitySubscriberMessage(world, cx, cz, unsubscriber));
        }
    }

    public static void syncSubscribers(ServerConnection serverConnection, String world, int cx, int cz) {
        ChunkKey key = new ChunkKey(world, cx, cz);
        synchronized (EntitiesLock.getEntitiesLock(key)) {
            if (!chunkSubscribers.containsKey(key) || !chunkSubscribers.get(key).contains(serverConnection)) {
                subscribe(serverConnection, world, cx, cz);
            }

            String[] subscribers = chunkSubscribers.get(key).stream().filter(subscriber -> subscriber != serverConnection).map(ServerConnection::getBungeeCordName).toArray(String[]::new);

            serverConnection.send(new EntitySubscribersSyncMessage(world, cx, cz, subscribers));
        }
    }

    public static void unsubscribeAll(ServerConnection serverConnection) {
        HashSet<ChunkKey> chunks = subscribedChunks.remove(serverConnection);
        if (chunks != null) {
            chunks.forEach(chunk -> unsubscribe(serverConnection, chunk));
        }
    }
}
