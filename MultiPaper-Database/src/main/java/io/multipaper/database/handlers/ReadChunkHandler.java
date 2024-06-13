package io.multipaper.database.handlers;

import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.RegionFileCache;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadChunkMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.DataMessage;

import java.io.File;

public class ReadChunkHandler {
    public static void handle(ServerConnection connection, ReadChunkMessage message) {
        AsyncIO.runAsync(() -> {
            byte[] b = RegionFileCache.getChunkDeflatedData(getWorldDir(message.world, message.path), message.cx, message.cz);

            if (b == null) {
                b = new byte[0];
            }

            connection.send(new DataMessage(b));
        });
    }

    static File getWorldDir(String world, String path) {
        File file = new File(world);

        if (world.endsWith("_nether")) {
            file = new File(file, "DIM-1");
        }

        if (world.endsWith("_the_end")) {
            file = new File(file, "DIM1");
        }

        return new File(file, path);
    }
}
