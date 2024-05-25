package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.LockChunkMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.SetChunkOwnerMessage;
import puregero.multipaper.database.ChunkSubscriptionManager;
import puregero.multipaper.database.ServerConnection;

public class LockChunkHandler {
    public static void handle(ServerConnection connection, LockChunkMessage message) {
        ServerConnection owner = ChunkSubscriptionManager.lock(connection, message.world, message.cx, message.cz);

        connection.send(new SetChunkOwnerMessage(message.world, message.cx, message.cz, owner.getBungeeCordName()));
    }
}
