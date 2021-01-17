package puregero.multipaper.server.locks;

import java.util.HashMap;
import java.util.HashSet;

public class ChunkLock {

    private static HashMap<String, String> locks = new HashMap<>();
    private static HashMap<String, Long> removeOnWrite = new HashMap<>();
    private static HashMap<String, String> removeOnWriter = new HashMap<>();

    private static String key(String world, int cx, int cz) {
        return world + "," + cx + "," + cz;
    }

    public static String getLockHolder(String world, int cx, int cz) {
        return locks.get(key(world, cx, cz));
    }

    public static String isBeingWritten(String world, int cx, int cz) {
        if (removeOnWrite.containsKey(key(world, cx, cz)) && removeOnWrite.get(key(world, cx, cz)) > System.currentTimeMillis() - 10*1000) {
            return removeOnWriter.get(key(world, cx, cz));
        }

        return null;
    }

    public static String lock(String holder, String world, int cx, int cz) {
        String prevHolder = locks.get(key(world, cx, cz));
        if (prevHolder != null && !prevHolder.equals(holder)) {
            return prevHolder;
        }

        locks.put(key(world, cx, cz), holder);

        return null;
    }

    public static boolean release(String holder, String world, int cx, int cz) {
        if (holder.equals(locks.get(key(world, cx, cz)))) {
            locks.remove(key(world, cx, cz));
            removeOnWrite.put(key(world, cx, cz), System.currentTimeMillis());
            removeOnWriter.put(key(world, cx, cz), holder);
            return true;
        }

        return false;
    }

    public static void chunkWritten(String world, int cx, int cz) {
        removeOnWriter.remove(key(world, cx, cz));
        removeOnWrite.remove(key(world, cx, cz));
    }
}
