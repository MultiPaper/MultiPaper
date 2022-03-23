package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.UploadFileMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.FileContentMessage;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class UploadFileHandler {
    public static void handle(ServerConnection connection, UploadFileMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                File file = new File("synced-server-files", message.path);
                FileLocker.writeBytes(file, message.data);
                file.setLastModified(message.lastModified);

                connection.sendReply(new BooleanMessageReply(true), message);

                if (message.immediatelySyncToOtherServers) {
                    connection.broadcastOthers(new FileContentMessage(message.path, message.lastModified, message.data));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
