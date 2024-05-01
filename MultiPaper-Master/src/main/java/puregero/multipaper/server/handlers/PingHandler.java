package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.PingMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.server.ServerConnection;

import java.util.concurrent.CompletableFuture;

public class PingHandler {
    public static void handle(ServerConnection connection, PingMessage message) {
        CompletableFuture.runAsync(() -> {
            connection.sendReply(new BooleanMessageReply(true), message);
        });
    }
}
