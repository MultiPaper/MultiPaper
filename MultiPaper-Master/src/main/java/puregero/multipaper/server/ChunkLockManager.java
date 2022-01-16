package puregero.multipaper.server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ChunkLockManager {

    private static final ConcurrentHashMap<ChunkKey, CompletableFuture<Void>> locks = new ConcurrentHashMap<>();

    public static void lockUntilWrite(String world, int cx, int cz) {
        locks.put(new ChunkKey(world, cx, cz), new CompletableFuture<Void>().completeOnTimeout(null, 60, TimeUnit.SECONDS));
    }

    public static void writtenChunk(String world, int cx, int cz) {
        CompletableFuture<Void> lock = locks.remove(new ChunkKey(world, cx, cz));

        if (lock != null) {
            lock.complete(null);
        }
    }

    public static void waitForLock(String world, int cx, int cz, Runnable callback) {
        CompletableFuture<Void> lock = locks.get(new ChunkKey(world, cx, cz));

        if (lock != null) {
            System.out.println("Waiting for lock on " + world + "," + cx + "," + cz + "...");
            lock.thenRun(callback);
        } else {
            callback.run();
        }
    }

}
