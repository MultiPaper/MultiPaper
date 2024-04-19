package puregero.multipaper.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
            safeWrite(file, bytes);
        } finally {
            synchronized (beingWritten) {
                beingWritten.remove(file);
            }
        }
    }

    private static void safeWrite(File file, byte[] bytes) throws IOException {
        File newFile = new File(file.getParentFile(), file.getName() + "_new");
        File oldFile = new File(file.getParentFile(), file.getName() + "_old");

        Files.write(newFile.toPath(), bytes);
        safeReplaceFile(file.toPath(), newFile.toPath(), oldFile.toPath());
    }

    private static void safeReplaceFile(Path file, Path newFile, Path oldFile) throws IOException {
        if (Files.exists(file)) {
            Files.move(file, oldFile, StandardCopyOption.REPLACE_EXISTING);
        }

        Files.move(newFile, file, StandardCopyOption.REPLACE_EXISTING);

        if (Files.exists(oldFile)) {
            Files.delete(oldFile);
        }
    }
}
