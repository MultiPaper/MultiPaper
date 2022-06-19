package puregero.multipaper.server;

import puregero.multipaper.mastermessagingprotocol.ChunkKey;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.AddChunkSubscriberMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ChunkSubscribersSyncMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.RemoveChunkSubscriberMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.SetChunkOwnerMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChunkSubscriptionManager {

    private static final Queue<List<ServerConnection>> serverPool = new LinkedList<>();

    // Index 0 in the list is the current chunk owning it
    private static final Map<ChunkKey, List<ServerConnection>> chunkOwners = new ConcurrentHashMap<>();
    private static final ReentrantReadWriteLock chunkOwnersLock = new ReentrantReadWriteLock();

    private static final Map<ChunkKey, List<ServerConnection>> chunkSubscribers = new ConcurrentHashMap<>();
    private static final ReentrantReadWriteLock chunkSubscribersLock = new ReentrantReadWriteLock();

    private static final Map<ServerConnection, HashSet<ChunkKey>> ownedChunks = new HashMap<>();
    private static final Map<ServerConnection, HashSet<ChunkKey>> subscribedChunks = new HashMap<>();

    public static ServerConnection getOwner(String world, int cx, int cz) {
        final ChunkKey key = new ChunkKey(world, cx, cz);
        chunkOwnersLock.readLock().lock();
        try {
            List<ServerConnection> serverConnections = chunkOwners.get(key);
            if (serverConnections != null && !serverConnections.isEmpty()) {
                return serverConnections.get(0);
            }
            return null;
        } finally {
            chunkOwnersLock.readLock().unlock();
        }
    }

    public static ServerConnection getOwnerOrSubscriber(String world, int cx, int cz) {
        ServerConnection owner = getOwner(world, cx, cz);
        if (owner != null) {
            return owner;
        }

        final ChunkKey key = new ChunkKey(world, cx, cz);
        chunkSubscribersLock.readLock().lock();
        try {
            List<ServerConnection> serverConnections = chunkSubscribers.get(key);
            if (serverConnections != null && !serverConnections.isEmpty()) {
                return serverConnections.get(0);
            }

            return null;
        } finally {
            chunkSubscribersLock.readLock().unlock();
        }
    }

    public static ServerConnection lock(ServerConnection serverConnection, String world, int cx, int cz) {
        return lock(serverConnection, world, cx, cz, false);
    }

    public static ServerConnection lock(ServerConnection serverConnection, String world, int cx, int cz, boolean force) {
        ChunkKey key = new ChunkKey(world, cx, cz);

        chunkOwnersLock.writeLock().lock();
        try {
            List<ServerConnection> serverConnections = chunkOwners.computeIfAbsent(key, key2 -> {
                synchronized (serverPool) {
                    List<ServerConnection> list = serverPool.poll();
                    if (list != null) {
                        return list;
                    }
                }
                return new ArrayList<>();
            });

            if (force && serverConnections.indexOf(serverConnection) != 0) {
                serverConnections.remove(serverConnection);
            }

            if (!serverConnections.contains(serverConnection)) {
                if (force) {
                    serverConnections.add(0, serverConnection);
                } else {
                    serverConnections.add(serverConnection);
                }

                ownedChunks.computeIfAbsent(serverConnection, k -> new HashSet<>()).add(key);

                if (serverConnections.size() == 1 || force) {
                    chunkSubscribersLock.readLock().lock();
                    try {
                        if (chunkSubscribers.get(key) != null) {
                            updateOwner(serverConnections.get(0), chunkSubscribers.get(key), key.world, key.x, key.z);
                        }
                    } finally {
                        chunkSubscribersLock.readLock().unlock();
                    }
                }
            }

            return serverConnections.get(0);
        } finally {
            chunkOwnersLock.writeLock().unlock();
        }
    }

    public static void unlock(ServerConnection serverConnection, String world, int cx, int cz) {
        unlock(serverConnection, new ChunkKey(world, cx, cz));
    }

    public static void unlock(ServerConnection serverConnection, ChunkKey key) {

        chunkOwnersLock.writeLock().lock();
        try {
            List<ServerConnection> serverConnections = chunkOwners.get(key);
            if (serverConnections != null) {
                for (int i = 0; i < serverConnections.size(); i++) {
                    if (serverConnections.get(i) == serverConnection) {
                        serverConnections.remove(i--);

                        if (i == -1) {
                            chunkSubscribersLock.readLock().lock();
                            try {
                                if (chunkSubscribers.get(key) != null) {
                                    if (serverConnections.isEmpty()) {
                                        updateOwner(null, chunkSubscribers.get(key), key.world, key.x, key.z);
                                    } else {
                                        updateOwner(serverConnections.get(0), chunkSubscribers.get(key), key.world, key.x, key.z);
                                    }
                                }
                            } finally {
                                chunkSubscribersLock.readLock().unlock();
                            }
                        }
                    }
                }
                if (serverConnections.isEmpty()) {
                    chunkOwners.remove(key);
                    synchronized (serverPool) {
                        serverPool.add(serverConnections);
                    }
                }
            }

            HashSet<ChunkKey> chunks = ownedChunks.get(serverConnection);
            if (chunks != null) {
                chunks.remove(key);
            }
        } finally {
            chunkOwnersLock.writeLock().unlock();
        }
    }

    private static void updateOwner(ServerConnection ownerConnection, List<ServerConnection> serverConnections, String world, int cx, int cz) {
        String owner = ownerConnection == null ? "" : ownerConnection.getBungeeCordName();
        for (ServerConnection connection : serverConnections) {
            connection.send(new SetChunkOwnerMessage(world, cx, cz, owner));
        }
    }


    public static void subscribe(ServerConnection serverConnection, String world, int cx, int cz) {
        ChunkKey key = new ChunkKey(world, cx, cz);

        chunkSubscribersLock.writeLock().lock();
        try {
            List<ServerConnection> serverConnections = chunkSubscribers.computeIfAbsent(key, key2 -> {
                synchronized (serverPool) {
                    List<ServerConnection> list = serverPool.poll();
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

            if (chunkOwners.get(key) != null && !chunkOwners.get(key).isEmpty()) {
                updateOwner(chunkOwners.get(key).get(0), Collections.singletonList(serverConnection), key.world, key.x, key.z);
            }
        } finally {
            chunkSubscribersLock.writeLock().unlock();
        }
    }

    public static void unsubscribe(ServerConnection serverConnection, String world, int cx, int cz) {
        unsubscribe(serverConnection, new ChunkKey(world, cx, cz));
    }

    public static void unsubscribe(ServerConnection serverConnection, ChunkKey key) {
        chunkSubscribersLock.writeLock().lock();
        try {
            List<ServerConnection> serverConnections = chunkSubscribers.get(key);
            if (serverConnections != null) {
                if (serverConnections.remove(serverConnection)) {
                    updateSubscriberRemoved(serverConnections, serverConnection, key.world, key.x, key.z);
                }
                if (serverConnections.isEmpty()) {
                    chunkSubscribers.remove(key);
                    synchronized (serverPool) {
                        serverPool.add(serverConnections);
                    }
                }
            }

            HashSet<ChunkKey> chunks = subscribedChunks.get(serverConnection);
            if (chunks != null) {
                chunks.remove(key);
            }
        } finally {
            chunkSubscribersLock.writeLock().unlock();
        }
    }

    private static void updateSubscriberAdd(List<ServerConnection> serverConnections, ServerConnection serverConnection, String world, int cx, int cz) {
        String subscriber = serverConnection.getBungeeCordName();
        for (ServerConnection connection : serverConnections) {
            if (connection != serverConnection) {
                connection.send(new AddChunkSubscriberMessage(world, cx, cz, subscriber));
            }
        }
    }

    private static void updateSubscriberRemoved(List<ServerConnection> serverConnections, ServerConnection serverConnection, String world, int cx, int cz) {
        String unsubscriber = serverConnection.getBungeeCordName();
        for (ServerConnection connection : serverConnections) {
            connection.send(new RemoveChunkSubscriberMessage(world, cx, cz, unsubscriber));
        }
    }

    public static void syncSubscribers(ServerConnection serverConnection, String world, int cx, int cz) {
        ChunkKey key = new ChunkKey(world, cx, cz);

        chunkSubscribersLock.readLock().lock();
        try {
            if (!chunkSubscribers.containsKey(key) || !chunkSubscribers.get(key).contains(serverConnection)) {
                subscribe(serverConnection, world, cx, cz);
            }

            String[] subscribers = chunkSubscribers.get(key).stream().filter(subscriber -> subscriber != serverConnection).map(ServerConnection::getBungeeCordName).toArray(String[]::new);

            serverConnection.send(new ChunkSubscribersSyncMessage(world, cx, cz, chunkOwners.get(key) != null && !chunkOwners.get(key).isEmpty() ? chunkOwners.get(key).get(0).getBungeeCordName() : "", subscribers));
        } finally {
            chunkSubscribersLock.readLock().unlock();
        }
    }

    public static void unsubscribeAndUnlockAll(ServerConnection serverConnection) {
        chunkOwnersLock.writeLock().lock();
        try {
            HashSet<ChunkKey> lockedChunkSet = ownedChunks.remove(serverConnection);
            if (lockedChunkSet != null) {
                lockedChunkSet.forEach(chunk -> unlock(serverConnection, chunk));
            }
        } finally {
            chunkOwnersLock.writeLock().unlock();
        }

        chunkSubscribersLock.writeLock().lock();
        try {
            HashSet<ChunkKey> subscribedChunkSet = subscribedChunks.remove(serverConnection);
            if (subscribedChunkSet != null) {
                subscribedChunkSet.forEach(chunk -> unsubscribe(serverConnection, chunk));
            }
        } finally {
            chunkSubscribersLock.writeLock().unlock();
        }
    }

    public static List<ServerConnection> getSubscribers(String world, int cx, int cz) {
        chunkSubscribersLock.readLock().lock();
        try {
            return chunkSubscribers.getOrDefault(new ChunkKey(world, cx, cz), Collections.emptyList());
        } finally {
            chunkSubscribersLock.readLock().unlock();
        }
    }
}