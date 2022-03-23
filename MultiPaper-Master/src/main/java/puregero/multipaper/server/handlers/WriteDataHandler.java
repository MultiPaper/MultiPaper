package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.WriteDataMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.DataUpdateMessage;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

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
