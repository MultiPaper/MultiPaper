package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.WillSaveChunkLaterMessage;
import puregero.multipaper.server.ChunkLockManager;
import puregero.multipaper.server.ServerConnection;

public class WillSaveChunkHandler {
    public static void handle(ServerConnection connection, WillSaveChunkLaterMessage message) {
        ChunkLockManager.lockUntilWrite(message.world, message.cx, message.cz);
    }
}
