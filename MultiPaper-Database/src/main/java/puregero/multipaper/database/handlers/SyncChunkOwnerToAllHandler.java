package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.SyncChunkOwnerToAllMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.SetChunkOwnerMessage;
import puregero.multipaper.database.ChunkSubscriptionManager;
import puregero.multipaper.database.ServerConnection;

public class SyncChunkOwnerToAllHandler {
    public static void handle(ServerConnection connection, SyncChunkOwnerToAllMessage message) {
        ServerConnection owner = ChunkSubscriptionManager.getOwner(message.world, message.cx, message.cz);

        ServerConnection.broadcastAll(new SetChunkOwnerMessage(message.world, message.cx, message.cz, owner == null ? "" : owner.getBungeeCordName()));
    }
}
