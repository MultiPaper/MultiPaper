package puregero.multipaper.server.handlers;

import lombok.extern.slf4j.Slf4j;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.ReadAdvancementMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.DataMessageReply;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ReadAdvancementsHandler {

    public static void handle(ServerConnection connection, ReadAdvancementMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                byte[] b = FileLocker.readBytes(new File(new File(message.world, "advancements"), message.uuid + ".json"));
                connection.sendReply(new DataMessageReply(b), message);
            } catch (IOException e) {
                log.error("Failed to read advancements", e);
            }
        });
    }
}
