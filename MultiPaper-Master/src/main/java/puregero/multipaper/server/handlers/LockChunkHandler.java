package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.LockChunkMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.SetChunkOwnerMessage;
import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

public class LockChunkHandler {
    public static void handle(ServerConnection connection, LockChunkMessage message) {
        ServerConnection owner = ChunkSubscriptionManager.lock(connection, message.world, message.cx, message.cz);

        connection.send(new SetChunkOwnerMessage(message.world, message.cx, message.cz, owner.getBungeeCordName()));
    }
}
