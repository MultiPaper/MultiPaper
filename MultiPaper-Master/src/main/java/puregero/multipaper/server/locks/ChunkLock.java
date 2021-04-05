package puregero.multipaper.server.locks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkLock {

    private static Map<String, Long> removeOnWrite = new ConcurrentHashMap<>();
    private static Map<String, String> removeOnWriter = new ConcurrentHashMap<>();

    private static String key(String world, int cx, int cz) {
        return world + "," + cx + "," + cz;
    }

    public static String isBeingWritten(String world, int cx, int cz) {
        if (removeOnWrite.containsKey(key(world, cx, cz)) && removeOnWrite.get(key(world, cx, cz)) > System.currentTimeMillis() - 30*1000) {
            return removeOnWriter.get(key(world, cx, cz));
        }

        return null;
    }

    public static void willWrite(String holder, String world, int cx, int cz) {
        removeOnWrite.put(key(world, cx, cz), System.currentTimeMillis());
        removeOnWriter.put(key(world, cx, cz), holder);
    }

    public static void chunkWritten(String world, int cx, int cz) {
        removeOnWriter.remove(key(world, cx, cz));
        removeOnWrite.remove(key(world, cx, cz));
    }
}
