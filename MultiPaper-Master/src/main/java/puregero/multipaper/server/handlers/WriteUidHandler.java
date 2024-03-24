package puregero.multipaper.server.handlers;

import lombok.extern.slf4j.Slf4j;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.WriteUidMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.server.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class WriteUidHandler {

    public static void handle(ServerConnection connection, WriteUidMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                File worldDir = new File(message.world);
                if (!worldDir.exists()) worldDir.mkdirs();
                Files.write(new File(worldDir, "uid.dat").toPath(), message.data);
                connection.sendReply(new BooleanMessageReply(true), message);
            } catch (IOException e) {
                log.error("Failed to write uid", e);
            }
        });
    }
}
