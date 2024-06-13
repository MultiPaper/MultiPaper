package io.multipaper.database.handlers;

import io.multipaper.database.FileLocker;
import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteDataMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.VoidMessage;

import java.io.File;
import java.io.IOException;

public class WriteDataHandler {
    public static void handle(ServerConnection connection, WriteDataMessage message) {
        AsyncIO.runAsync(() -> {
            try {
                FileLocker.writeBytes(new File(message.path), message.data);
                connection.send(new VoidMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
