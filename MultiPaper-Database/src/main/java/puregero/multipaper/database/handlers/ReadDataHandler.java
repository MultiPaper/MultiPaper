package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.ReadDataMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.DataMessageReply;
import puregero.multipaper.database.FileLocker;
import puregero.multipaper.database.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ReadDataHandler {
    public static void handle(ServerConnection connection, ReadDataMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                byte[] b = FileLocker.readBytes(new File(message.path));
                connection.sendReply(new DataMessageReply(b), message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
