package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.ReadStatsMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.DataMessageReply;
import puregero.multipaper.database.FileLocker;
import puregero.multipaper.database.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ReadStatsHandler {
    public static void handle(ServerConnection connection, ReadStatsMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                byte[] b = FileLocker.readBytes(new File(new File(message.world, "stats"), message.uuid + ".json"));
                connection.sendReply(new DataMessageReply(b), message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
