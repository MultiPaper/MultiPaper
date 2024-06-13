package io.multipaper.database.handlers;

import io.multipaper.database.FileLocker;
import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadPlayerMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.DataMessage;

import java.io.File;
import java.io.IOException;

public class ReadPlayerHandler {
    public static void handle(ServerConnection connection, ReadPlayerMessage message) {
        AsyncIO.runAsync(() -> {
            try {
                byte[] b = FileLocker.readBytes(new File(new File(message.world, "playerdata"), message.uuid + ".dat"));
                connection.send(new DataMessage(b));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
