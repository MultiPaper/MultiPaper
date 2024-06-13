package io.multipaper.database.handlers;

import io.multipaper.database.FileLocker;
import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadStatsMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.DataMessage;

import java.io.File;
import java.io.IOException;

public class ReadStatsHandler {
    public static void handle(ServerConnection connection, ReadStatsMessage message) {
        AsyncIO.runAsync(() -> {
            try {
                byte[] b = FileLocker.readBytes(new File(new File(message.world, "stats"), message.uuid + ".json"));
                connection.send(new DataMessage(b));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
