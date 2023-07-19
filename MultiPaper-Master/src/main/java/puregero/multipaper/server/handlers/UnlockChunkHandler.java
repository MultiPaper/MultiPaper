package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.UnlockChunkMessage;
import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

public class UnlockChunkHandler {

    public static void handle(ServerConnection connection, UnlockChunkMessage message) {
        ChunkSubscriptionManager.unlock(connection, message.world, message.cx, message.cz);
    }
}
