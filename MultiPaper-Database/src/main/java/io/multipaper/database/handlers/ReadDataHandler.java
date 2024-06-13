package io.multipaper.database.handlers;

import io.multipaper.database.FileLocker;
import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadDataMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.DataMessage;

import java.io.File;
import java.io.IOException;

public class ReadDataHandler {
    public static void handle(ServerConnection connection, ReadDataMessage message) {
        AsyncIO.runAsync(() -> {
            try {
                byte[] b = FileLocker.readBytes(new File(message.path));
                connection.send(new DataMessage(b));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
