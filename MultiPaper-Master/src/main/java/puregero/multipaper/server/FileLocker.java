package puregero.multipaper.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Put any files being written into a hashmap, and if they're read while they're
 * being written, return the bytes that are being written instead of reading
 * from the file.
 */
public class FileLocker {

    private static final HashMap<File, byte[]> beingWritten = new HashMap<>();
    private static final HashMap<File, CompletableFuture<Void>> locks = new HashMap<>();

    public static CompletableFuture<CompletableFuture<Void>> createLockAsync(File file) {
        synchronized (locks) {
            if (locks.containsKey(file)) {
                return locks.get(file).thenCompose(value -> createLockAsync(file));
            }

            CompletableFuture<Void> lock = new CompletableFuture<>();

            locks.put(file, lock.thenRun(() -> {
                synchronized (locks) {
                    locks.remove(file);
                }
            }));

            return CompletableFuture.completedFuture(lock);
        }
    }

    public static byte[] readBytes(File file) throws IOException {
        synchronized (beingWritten) {
            if (beingWritten.containsKey(file)) {
                return beingWritten.get(file);
            }
        }

        return !file.isFile() ? new byte[0] : Files.readAllBytes(file.toPath());
    }

    public static void writeBytes(File file, byte[] bytes) throws IOException {
        synchronized (beingWritten) {
            while (beingWritten.containsKey(file)) {
                try {
                    // Wait 1 millisecond for file to finish being written
                    beingWritten.wait(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            beingWritten.put(file, bytes);
        }

        try {
            file.getParentFile().mkdirs();
            Files.write(file.toPath(), bytes);
        } finally {
            synchronized (beingWritten) {
                beingWritten.remove(file);
            }
        }
    }
}
