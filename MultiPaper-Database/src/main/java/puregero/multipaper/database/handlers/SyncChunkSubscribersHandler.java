package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.SyncChunkSubscribersMessage;
import puregero.multipaper.database.ChunkSubscriptionManager;
import puregero.multipaper.database.ServerConnection;

public class SyncChunkSubscribersHandler {
    public static void handle(ServerConnection connection, SyncChunkSubscribersMessage message) {
        ChunkSubscriptionManager.syncSubscribers(connection, message.world, message.cx, message.cz);
    }
}
