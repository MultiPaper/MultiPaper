package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.DownloadFileMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.FileContentMessage;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class DownloadFileHandler {
    public static void handle(ServerConnection connection, DownloadFileMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                File file = new File("synced-server-files", message.path);
                byte[] b = FileLocker.readBytes(file);
                connection.sendReply(new FileContentMessage(message.path, file.lastModified(), b), message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
