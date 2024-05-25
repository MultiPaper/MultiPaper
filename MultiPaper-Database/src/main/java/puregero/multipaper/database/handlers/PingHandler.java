package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.PingMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.database.ServerConnection;

import java.util.concurrent.CompletableFuture;

public class PingHandler {
    public static void handle(ServerConnection connection, PingMessage message) {
        CompletableFuture.runAsync(() -> {
            connection.sendReply(new BooleanMessageReply(true), message);
        });
    }
}
