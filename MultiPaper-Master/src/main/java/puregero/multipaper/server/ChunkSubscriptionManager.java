package puregero.multipaper.server;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkSubscriptionManager {

    private static final Queue<List<ServerConnection>> objectPool = new LinkedList<>();

    // Index 0 in the list is the current chunk owning it
    private static final Map<ChunkKey, List<ServerConnection>> chunkLocks = new ConcurrentHashMap<>();
    private static final Map<ChunkKey, List<ServerConnection>> chunkSubscribers = new ConcurrentHashMap<>();

    private static final Map<ServerConnection, HashSet<ChunkKey>> lockedChunks = new HashMap<>();
    private static final Map<ServerConnection, HashSet<ChunkKey>> subscribedChunks = new HashMap<>();

    public static ServerConnection getOwner(String world, int cx, int cz) {
        synchronized (chunkLocks) {
            List<ServerConnection> serverConnections = chunkLocks.get(new ChunkKey(world, cx, cz));
            if (serverConnections != null && !serverConnections.isEmpty()) {
                return serverConnections.get(0);
            }
        }
        return null;
    }

    public static ServerConnection getOwnerOrSubscriber(String world, int cx, int cz) {
        ServerConnection owner = getOwner(world, cx, cz);
        if (owner != null) {
            return owner;
        }

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
        synchronized (chunkLocks) {
            List<ServerConnection> serverConnections = chunkLocks.computeIfAbsent(key, key2 -> {
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

                if (serverConnections.size() == 1) {
                    synchronized (chunkSubscribers) {
                        if (chunkSubscribers.get(key) != null) {
                            updateOwner(serverConnections.get(0), chunkSubscribers.get(key), key.name, key.x, key.z);
                        }
                    }
                }
            }

            return serverConnections.get(0);
        }
    }

    public static void unlock(ServerConnection serverConnection, String world, int cx, int cz) {
        unlock(serverConnection, new ChunkKey(world, cx, cz));
    }

    public static void unlock(ServerConnection serverConnection, ChunkKey key) {
        synchronized (chunkLocks) {
            List<ServerConnection> serverConnections = chunkLocks.get(key);
            if (serverConnections != null) {
                for (int i = 0; i < serverConnections.size(); i++) {
                    if (serverConnections.get(i) == serverConnection) {
                        serverConnections.remove(i--);

                        if (i == -1) {
                            synchronized (chunkSubscribers) {
                                if (chunkSubscribers.get(key) != null) {
                                    if (serverConnections.isEmpty()) {
                                        updateOwner(null, chunkSubscribers.get(key), key.name, key.x, key.z);
                                    } else {
                                        updateOwner(serverConnections.get(0), chunkSubscribers.get(key), key.name, key.x, key.z);
                                    }
                                }
                            }
                        }
                    }
                }
                if (serverConnections.isEmpty()) {
                    chunkLocks.remove(key);
                    synchronized (objectPool) {
                        objectPool.add(serverConnections);
                    }
                }
            }

            HashSet<ChunkKey> chunks = lockedChunks.get(serverConnection);
            if (chunks != null) {
                chunks.remove(key);
            }
        }
    }

    private static void updateOwner(ServerConnection ownerConnection, List<ServerConnection> serverConnections, String world, int cx, int cz) {
        String owner = ownerConnection == null ? "" : ownerConnection.getBungeeCordName();
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

            if (!serverConnections.isEmpty()) {
                List<ServerConnection> singletonList = Collections.singletonList(serverConnection);
                for (ServerConnection subscriber : serverConnections) {
                    if (subscriber != serverConnection) {
                        updateSubscriberAdd(singletonList, subscriber, world, cx, cz);
                    }
                }
            }

            if (!serverConnections.contains(serverConnection)) {
                serverConnections.add(serverConnection);
                updateSubscriberAdd(serverConnections, serverConnection, world, cx, cz);
                subscribedChunks.computeIfAbsent(serverConnection, k -> new HashSet<>()).add(key);
            }

            if (chunkLocks.get(key) != null && !chunkLocks.get(key).isEmpty()) {
                updateOwner(chunkLocks.get(key).get(0), Collections.singletonList(serverConnection), key.name, key.x, key.z);
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
                        out.writeUTF("chunkSubscribe");
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
                    out.writeUTF("chunkUnsubscribe");
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

    public static void unsubscribeAndUnlockAll(ServerConnection serverConnection) {
        synchronized (chunkLocks) {
            HashSet<ChunkKey> chunks = lockedChunks.remove(serverConnection);
            if (chunks != null) {
                chunks.forEach(chunk -> unlock(serverConnection, chunk));
            }
        }
        synchronized (chunkSubscribers) {
            HashSet<ChunkKey> chunks = subscribedChunks.remove(serverConnection);
            if (chunks != null) {
                chunks.forEach(chunk -> unsubscribe(serverConnection, chunk));
            }
        }
    }

    public static List<ServerConnection> getSubscribers(String world, int cx, int cz) {
        return chunkSubscribers.getOrDefault(new ChunkKey(world, cx, cz), Collections.emptyList());
    }
}
