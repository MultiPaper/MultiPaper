package io.multipaper.database.handlers;

import io.multipaper.database.FileLocker;
import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteAdvancementsMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.BooleanMessage;

import java.io.File;
import java.io.IOException;

public class WriteAdvancementsHandler {
    public static void handle(ServerConnection connection, WriteAdvancementsMessage message) {
        AsyncIO.runAsync(() -> {
            try {
                FileLocker.writeBytes(new File(new File(message.world, "advancements"), message.uuid + ".json"), message.data);

                connection.send(new BooleanMessage(true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
