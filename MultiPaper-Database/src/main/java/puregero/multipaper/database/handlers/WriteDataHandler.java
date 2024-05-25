package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.WriteDataMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.DataUpdateMessage;
import puregero.multipaper.database.FileLocker;
import puregero.multipaper.database.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class WriteDataHandler {
    public static void handle(ServerConnection connection, WriteDataMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                FileLocker.writeBytes(new File(message.path), message.data);
                connection.sendReply(new BooleanMessageReply(true), message);

                if (message.path.contains("scoreboard")) {
                    // Scoreboards are synced with other methods
                    return;
                }

                connection.broadcastOthers(new DataUpdateMessage(message.path, message.data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
