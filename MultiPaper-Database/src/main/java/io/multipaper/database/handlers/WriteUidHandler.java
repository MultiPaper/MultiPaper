package io.multipaper.database.handlers;

import io.multipaper.database.FileLocker;
import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteUidMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.BooleanMessage;

import java.io.File;
import java.io.IOException;

public class WriteUidHandler {
    public static void handle(ServerConnection connection, WriteUidMessage message) {
        AsyncIO.runAsync(() -> {
            try {
                FileLocker.writeBytes(new File(message.world, "uid.dat"), message.data);
                connection.send(new BooleanMessage(true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
