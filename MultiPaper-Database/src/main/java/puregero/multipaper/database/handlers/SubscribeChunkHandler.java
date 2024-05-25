package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.SubscribeChunkMessage;
import puregero.multipaper.database.ChunkSubscriptionManager;
import puregero.multipaper.database.ServerConnection;

public class SubscribeChunkHandler {
    public static void handle(ServerConnection connection, SubscribeChunkMessage message) {
        ChunkSubscriptionManager.subscribe(connection, message.world, message.cx, message.cz);
    }
}
