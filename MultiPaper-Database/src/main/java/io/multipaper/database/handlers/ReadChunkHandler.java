package io.multipaper.database.handlers;

import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.RegionFileCache;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadChunkMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.DataMessage;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ReadChunkHandler {
    public static void handle(ServerConnection connection, ReadChunkMessage message) {
        AsyncIO.runAsync(() -> {
            byte[] b = RegionFileCache.getChunkDeflatedData(ReadChunkHandler.validateFile(message.path), message.cx, message.cz);

            if (b == null) {
                b = new byte[0];
            }

            connection.send(new DataMessage(b));
        });
    }

    private static final Set<String> validFiles = new HashSet<>();

    static File validateFile(String path) {
        File file = new File(path);

        if (validFiles.contains(path)) {
            // Cache results as canonical path lookups are expensive
            return file;
        }

        try {
            File thisDirectory = new File(".");
            if (file.getCanonicalPath().startsWith(thisDirectory.getCanonicalPath())) {
                validFiles.add(path);
            } else {
                throw new IllegalArgumentException("Path goes out of the scope of the working directory: " + path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }
}
