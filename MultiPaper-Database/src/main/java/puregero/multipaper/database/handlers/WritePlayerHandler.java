package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.WritePlayerMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.database.FileLocker;
import puregero.multipaper.database.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class WritePlayerHandler {
    public static void handle(ServerConnection connection, WritePlayerMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                FileLocker.writeBytes(new File(new File(message.world, "playerdata"), message.uuid + ".dat"), message.data);
                connection.sendReply(new BooleanMessageReply(true), message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
