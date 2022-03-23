package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.SyncChunkOwnerToAllMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.SetChunkOwnerMessage;
import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

public class SyncChunkOwnerToAllHandler {
    public static void handle(ServerConnection connection, SyncChunkOwnerToAllMessage message) {
        ServerConnection owner = ChunkSubscriptionManager.getOwner(message.world, message.cx, message.cz);

        ServerConnection.broadcastAll(new SetChunkOwnerMessage(message.world, message.cx, message.cz, owner == null ? "" : owner.getBungeeCordName()));
    }
}
