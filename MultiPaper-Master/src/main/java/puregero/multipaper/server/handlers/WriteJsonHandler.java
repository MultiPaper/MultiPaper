package puregero.multipaper.server.handlers;

import lombok.extern.slf4j.Slf4j;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.WriteJsonMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.server.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class WriteJsonHandler {

    public static final HashMap<String, byte[]> writesInProgress = new HashMap<>();
    private static final Object writingLock = new Object();

    public static void handle(ServerConnection connection, WriteJsonMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                synchronized (writingLock) {
                    synchronized (writesInProgress) {
                        writesInProgress.put(message.file, message.data);
                    }

                    Files.write(new File(message.file).toPath(), message.data);

                    synchronized (writesInProgress) {
                        writesInProgress.remove(message.file);
                    }
                }

                connection.sendReply(new BooleanMessageReply(true), message);
            } catch (IOException e) {
                log.error("Failed to write json", e);
            }
        });
    }
}
