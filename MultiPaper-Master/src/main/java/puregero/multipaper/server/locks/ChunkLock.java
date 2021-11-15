package puregero.multipaper.server.locks;

import puregero.multipaper.server.ServerConnection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkLock {

    private static final Queue<List<ServerConnection>> objectPool = new LinkedList<>();

    // Index 0 in the list is the current chunk ticking it
    private static final Map<String, List<ServerConnection>> chunkTickers = new ConcurrentHashMap<>();

    private static String key(String world, int cx, int cz) {
        return world + "," + cx + "," + cz;
    }

    public static boolean addTicker(ServerConnection serverConnection, String world, int cx, int cz) {
        synchronized (chunkTickers) {
            List<ServerConnection> serverConnections = chunkTickers.computeIfAbsent(key(world, cx, cz), key -> {
                synchronized (objectPool) {
                    List<ServerConnection> list = objectPool.poll();
                    if (list != null) {
                        return list;
                    }
                }
                return new ArrayList<>();
            });

            serverConnections.add(serverConnection);

            return serverConnections.size() == 1;
        }
    }

    public static void chunkWritten(ServerConnection serverConnection, String world, int cx, int cz) {
        synchronized (chunkTickers) {
            List<ServerConnection> serverConnections = chunkTickers.get(key(world, cx, cz));
            if (serverConnections != null) {
                for (int i = 0; i < serverConnections.size(); i++) {
                    if (serverConnections.get(i) == serverConnection) {
                        serverConnections.remove(i--);

                        if (i == -1 && !serverConnections.isEmpty()) {
                            // TODO Tell next of kin it's their time to shine
                        }
                    }
                }
                if (serverConnections.isEmpty()) {
                    chunkTickers.remove(key(world, cx, cz));
                    objectPool.add(serverConnections);
                }
            }
        }
    }
}
