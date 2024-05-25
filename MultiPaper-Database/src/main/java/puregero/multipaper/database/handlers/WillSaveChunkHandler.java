package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.WillSaveChunkLaterMessage;
import puregero.multipaper.database.ChunkLockManager;
import puregero.multipaper.database.ServerConnection;

public class WillSaveChunkHandler {
    public static void handle(ServerConnection connection, WillSaveChunkLaterMessage message) {
        ChunkLockManager.lockUntilWrite(message.world, message.cx, message.cz);
    }
}
