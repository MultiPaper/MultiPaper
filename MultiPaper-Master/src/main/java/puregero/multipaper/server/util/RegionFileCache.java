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

public class RegionFileCache {

    private static final int MAX_CACHE_SIZE = 256;

    private static final Map<File, Reference<RegionFile>> cache = new HashMap<>();
    private static final Queue<File> queue = new LinkedList<>();

    private RegionFileCache() {
    }

    public static synchronized boolean isRegionFileOpen(File regionDir, int chunkX, int chunkZ) {
        File file = new File(regionDir, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");

        file = canonical(file);

        Reference<RegionFile> ref = cache.get(file);

        if (ref != null && ref.get() != null) {
            return true;
        }

        return false;
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

    public static synchronized RegionFile getRegionFile(File regionDir, int chunkX, int chunkZ) {
        File file = new File(regionDir, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");

        file = canonical(file);

        Reference<RegionFile> ref = cache.get(file);

        if (ref != null && ref.get() != null) {
            return ref.get();
        }

        if (!regionDir.exists()) {
            regionDir.mkdirs();
        }

        if (ref == null) {
            queue.add(file);
        }

        if (cache.size() >= MAX_CACHE_SIZE) {
            clearOne();
        }

        RegionFile reg = new RegionFile(file);
        cache.put(file, new SoftReference<>(reg));
        return reg;
    }

    private static synchronized void clearOne() {
        File remove = queue.remove();
        Reference<RegionFile> refRemove = cache.remove(remove);
        try {
            RegionFile removeFile = refRemove.get();
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
        return r.getChunkDataInputStream(chunkX & 31, chunkZ & 31);
    }

    public static DataOutputStream getChunkDataOutputStream(File basePath, int chunkX, int chunkZ) {
        RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
        return r.getChunkDataOutputStream(chunkX & 31, chunkZ & 31);
    }

    public static byte[] getChunkDeflatedData(File basePath, int chunkX, int chunkZ) {
        RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
        return r.getDeflatedBytes(chunkX & 31, chunkZ & 31);
    }

    public static void putChunkDeflatedData(File basePath, int chunkX, int chunkZ, byte[] data) {
        RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
        r.putDeflatedBytes(chunkX & 31, chunkZ & 31, data);
    }
}
