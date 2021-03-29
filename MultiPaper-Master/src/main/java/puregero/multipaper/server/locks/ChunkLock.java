package puregero.multipaper.server.locks;

import java.util.*;

public class ChunkLock {

    private static HashMap<String, Long> removeOnWrite = new HashMap<>();
    private static HashMap<String, String> removeOnWriter = new HashMap<>();

    private static String key(String world, int cx, int cz) {
        return world + "," + cx + "," + cz;
    }

    public static String isBeingWritten(String world, int cx, int cz) {
        if (removeOnWrite.containsKey(key(world, cx, cz)) && removeOnWrite.get(key(world, cx, cz)) > System.currentTimeMillis() - 60*1000) {
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
