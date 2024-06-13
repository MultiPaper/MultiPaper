package io.multipaper.database.handlers;

import io.multipaper.database.FileLocker;
import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WritePlayerMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.VoidMessage;

import java.io.File;
import java.io.IOException;

public class WritePlayerHandler {
    public static void handle(ServerConnection connection, WritePlayerMessage message) {
        AsyncIO.runAsync(() -> {
            try {
                FileLocker.writeBytes(new File(new File(message.world, "playerdata"), message.uuid + ".dat"), message.data);
                connection.send(new VoidMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
