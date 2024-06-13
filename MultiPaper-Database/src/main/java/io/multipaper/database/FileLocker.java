package io.multipaper.database;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

/**
 * Put any files being written into a hashmap, and if they're read while they're
 * being written, return the bytes that are being written instead of reading
 * from the file.
 */
public class FileLocker {

    private static final Map<String, WeakReference<StampedLock>> locks = new ConcurrentHashMap<>();

    private static void cleanupLocks() {
        System.out.println("locks size before cleanup: " + locks.size());
        locks.entrySet().removeIf(e -> e.getValue().get() == null);
        System.out.println("locks size after cleanup: " + locks.size());
    }

    static {
        Thread.ofVirtual().name("FileLocker-Cleanup").start(() -> {
            while (true) {
                cleanupLocks();
                try {
                    Thread.sleep(300_000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static String key(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static StampedLock getLock(File file) {
        return locks.compute(key(file), (k, v) -> {
            return v == null || v.get() == null ? new WeakReference<>(new StampedLock()) : v;
        }).get();
    }

    public static <T> T readLock(File file, SupplierThrowingIOException<T> supplier) throws IOException {
        StampedLock lock = getLock(file);
        lock.readLock();
        try {
            return supplier.get();
        } finally {
            lock.tryUnlockRead();
        }
    }

    public static void writeLock(File file, RunnableThrowingIOException runnable) throws IOException {
        StampedLock lock = getLock(file);
        lock.writeLock();
        try {
            runnable.run();
        } finally {
            lock.tryUnlockWrite();
        }
    }

    public static byte[] readBytes(File file) throws IOException {
        return readLock(file, () -> {
            return !file.isFile() ? new byte[0] : Files.readAllBytes(file.toPath());
        });
    }

    public static void writeBytes(File file, byte[] bytes) throws IOException {
        writeLock(file, () -> {
            file.getParentFile().mkdirs();
            safeWrite(file, bytes);
        });
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

    @FunctionalInterface
    public interface RunnableThrowingIOException {
        void run() throws IOException;
    }

    @FunctionalInterface
    public interface SupplierThrowingIOException<T> {
        T get() throws IOException;
    }

}
