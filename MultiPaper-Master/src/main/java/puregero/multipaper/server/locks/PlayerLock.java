package puregero.multipaper.server.locks;

import java.util.HashMap;

public class PlayerLock {

    private static HashMap<String, String> locks = new HashMap<>();

    public static String getLockHolder(String uuid) {
        return locks.get(uuid);
    }

    public static void lock(String holder, String uuid) {
        locks.put(uuid, holder);
    }

    public static void release(String holder, String uuid) {
        if (holder.equals(locks.get(uuid))) {
            locks.remove(uuid);
        }
    }

}
