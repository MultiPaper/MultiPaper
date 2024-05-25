package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.UnlockChunkMessage;
import puregero.multipaper.database.ChunkSubscriptionManager;
import puregero.multipaper.database.ServerConnection;

public class UnlockChunkHandler {
    public static void handle(ServerConnection connection, UnlockChunkMessage message) {
        ChunkSubscriptionManager.unlock(connection, message.world, message.cx, message.cz);
    }
}
