package io.multipaper.database.handlers;

import io.multipaper.database.FileLocker;
import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteStatsMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.VoidMessage;

import java.io.File;
import java.io.IOException;

public class WriteStatsHandler {
    public static void handle(ServerConnection connection, WriteStatsMessage message) {
        AsyncIO.runAsync(() -> {
            try {
                FileLocker.writeBytes(new File(new File(message.world, "stats"), message.uuid + ".json"), message.data);
                connection.send(new VoidMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
