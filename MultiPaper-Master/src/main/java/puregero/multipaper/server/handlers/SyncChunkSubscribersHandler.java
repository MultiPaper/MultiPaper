package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.SyncChunkSubscribersMessage;
import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

public class SyncChunkSubscribersHandler {

    public static void handle(ServerConnection connection, SyncChunkSubscribersMessage message) {
        ChunkSubscriptionManager.syncSubscribers(connection, message.world, message.cx, message.cz);
    }
}
