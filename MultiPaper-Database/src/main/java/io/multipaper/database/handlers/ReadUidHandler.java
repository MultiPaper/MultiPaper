package io.multipaper.database.handlers;

import io.multipaper.database.FileLocker;
import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadUidMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.DataMessage;

import java.io.File;
import java.io.IOException;

public class ReadUidHandler {
    public static void handle(ServerConnection connection, ReadUidMessage message) {
        AsyncIO.runAsync(() -> {
            try {
                byte[] b = FileLocker.readBytes(new File(message.world, "uid.dat"));
                connection.send(new DataMessage(b));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
