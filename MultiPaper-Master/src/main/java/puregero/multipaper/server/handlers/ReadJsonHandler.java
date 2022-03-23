package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.ReadJsonMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.DataMessageReply;
import puregero.multipaper.server.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.concurrent.CompletableFuture;

public class ReadJsonHandler {
    public static void handle(ServerConnection connection, ReadJsonMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                synchronized (WriteJsonHandler.writesInProgress) {
                    if (WriteJsonHandler.writesInProgress.containsKey(message.file)) {
                        byte[] b = WriteJsonHandler.writesInProgress.get(message.file);
                        connection.sendReply(new DataMessageReply(b), message);
                        return;
                    }
                }

                try {
                    byte[] b = Files.readAllBytes(new File(message.file).toPath());
                    connection.sendReply(new DataMessageReply(b), message);
                } catch (NoSuchFileException e) {
                    connection.sendReply(new DataMessageReply(new byte[0]), message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
