package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.ChunkChangedStatusMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ServerChangedChunkStatusMessage;
import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

public class ChunkChangedStatusHandler {
    public static void handle(ServerConnection connection, ChunkChangedStatusMessage message) {
        for (ServerConnection subscriber : ChunkSubscriptionManager.getSubscribers(message.world, message.cx, message.cz)) {
            subscriber.send(new ServerChangedChunkStatusMessage(message.world, message.cx, message.cz, message.status, connection.getBungeeCordName()));
        }
    }
}
