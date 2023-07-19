package puregero.multipaper.server.util;

/*
 ** 2011 January 5
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */

/*
 * 2011 February 16
 *
 * This source code is based on the work of Scaevolus (see notice above).
 * It has been slightly modified by Mojang AB to limit the maximum cache
 * size (relevant to extremely big worlds on Linux systems with limited
 * number of file handles). The region files are postfixed with ".mcr"
 * (Minecraft region file) instead of ".data" to differentiate from the
 * original McRegion files.
 *
 */

// A simple cache and wrapper for efficiently multiple RegionFiles simultaneously.

import java.io.*;
import java.lang.ref.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RegionFileCache {

    private static final int MAX_CACHE_SIZE = Integer.getInteger("max.regionfile.cache.size", 256);

    private static final LinkedHashMap<File, Reference<RegionFile>> cache = new LinkedHashMap<>(16, 0.75f, true);

    private RegionFileCache() {
    }

    public static synchronized boolean isRegionFileOpen(File regionDir, int chunkX, int chunkZ) {
        File file = new File(regionDir, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");

        file = canonical(file);

        Reference<RegionFile> ref = cache.get(file);

        return ref != null && ref.get() != null;
    }

    private static File canonical(File file) {
        try {
            // Remove any .'s and ..'s
            return new File(file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
            return file;
        }
    }
    
    private static File getFileForRegionFile(File regionDir, int chunkX, int chunkZ) {
        return new File(regionDir, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");
    }

    public static synchronized RegionFile getRegionFileIfExists(File regionDir, int chunkX, int chunkZ) {
        File file = getFileForRegionFile(regionDir, chunkX, chunkZ);

        file = canonical(file);

        Reference<RegionFile> ref = cache.get(file);

        if (ref != null && ref.get() != null) {
            return ref.get();
        }

        if (file.isFile()) {
            return getRegionFile(regionDir, chunkX, chunkZ);
        } else {
            return null;
        }
    }

    public static synchronized RegionFile getRegionFile(File regionDir, int chunkX, int chunkZ) {
        File file = getFileForRegionFile(regionDir, chunkX, chunkZ);

        file = canonical(file);

        Reference<RegionFile> ref = cache.get(file);

        if (ref != null && ref.get() != null) {
            return ref.get();
        }

        if (!regionDir.exists()) {
            regionDir.mkdirs();
        }

        if (cache.size() >= MAX_CACHE_SIZE) {
            clearOne();
        }

        RegionFile reg = new RegionFile(file);
        cache.put(file, new SoftReference<>(reg));
        return reg;
    }

    private static synchronized void clearOne() {
        Map.Entry<File, Reference<RegionFile>> clearEntry = cache.entrySet().iterator().next();
        cache.remove(clearEntry.getKey());
        try {
            RegionFile removeFile = clearEntry.getValue().get();
            if (removeFile != null) {
                removeFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getSizeDelta(File basePath, int chunkX, int chunkZ) {
        RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
        return r.getSizeDelta();
    }

    public static DataInputStream getChunkDataInputStream(File basePath, int chunkX, int chunkZ) {
        RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
        if (r != null) {
            return r.getChunkDataInputStream(chunkX, chunkZ);
        } else {
            return null;
        }
    }

    public static DataOutputStream getChunkDataOutputStream(File basePath, int chunkX, int chunkZ) {
        RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
        return r.getChunkDataOutputStream(chunkX, chunkZ);
    }

    public static CompletableFuture<byte[]> getChunkDeflatedDataAsync(File basePath, int chunkX, int chunkZ) {
        RegionFile r = getRegionFileIfExists(basePath, chunkX, chunkZ);
        if (r != null) {
            return r.submitTask(regionFile -> regionFile.getDeflatedBytes(chunkX, chunkZ));
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    private static byte[] getChunkDeflatedData(File basePath, int chunkX, int chunkZ) {
        try {
            RegionFile r = getRegionFileIfExists(basePath, chunkX, chunkZ);
            if (r != null) {
                return r.getDeflatedBytes(chunkX, chunkZ);
            } else {
                return null;
            }
        } catch (Throwable throwable) {
            System.err.println("Error when trying to read chunk " + chunkX + "," + chunkZ + " in " + basePath);
            throw throwable;
        }
    }

    public static CompletableFuture<Void> putChunkDeflatedDataAsync(File basePath, int chunkX, int chunkZ, byte[] data) {
        RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
        return r.submitTask(regionFile -> {
            regionFile.putDeflatedBytes(chunkX, chunkZ, data);
            return null;
        });
    }

    private static void putChunkDeflatedData(File basePath, int chunkX, int chunkZ, byte[] data) {
        try {
            RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
            r.putDeflatedBytes(chunkX, chunkZ, data);
        } catch (Throwable throwable) {
            System.err.println("Error when trying to write chunk " + chunkX + "," + chunkZ + " in " + basePath);
            throw throwable;
        }
    }
}
