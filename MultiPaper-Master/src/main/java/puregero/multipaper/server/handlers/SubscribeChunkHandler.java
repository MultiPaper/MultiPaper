package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.SubscribeChunkMessage;
import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

import java.io.IOException;

public class SubscribeChunkHandler {
    public static void handle(ServerConnection connection, SubscribeChunkMessage message) {
        ChunkSubscriptionManager.subscribe(connection, message.world, message.cx, message.cz);
    }
}
