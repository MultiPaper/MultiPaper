package puregero.multipaper.server;

import puregero.multipaper.mastermessagingprotocol.ChunkKey;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class EntitiesLockManager {

    private static final ConcurrentHashMap<ChunkKey, CompletableFuture<Void>> locks = new ConcurrentHashMap<>();

    public static void lockUntilWrite(String world, int cx, int cz) {
        CompletableFuture<Void> lock = new CompletableFuture<Void>().completeOnTimeout(null, 60, TimeUnit.SECONDS);
        CompletableFuture<Void> oldLock = locks.put(new ChunkKey(world, cx, cz), lock);

        if (oldLock != null) {
            lock.thenRun(() -> oldLock.complete(null));
        }
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
            lock.thenRun(callback);
        } else {
            callback.run();
        }
    }
}
