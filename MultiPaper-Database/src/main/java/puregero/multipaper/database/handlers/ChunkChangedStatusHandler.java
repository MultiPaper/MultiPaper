package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.ChunkChangedStatusMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.ServerChangedChunkStatusMessage;
import puregero.multipaper.database.ChunkSubscriptionManager;
import puregero.multipaper.database.ServerConnection;

public class ChunkChangedStatusHandler {
    public static void handle(ServerConnection connection, ChunkChangedStatusMessage message) {
        for (ServerConnection subscriber : ChunkSubscriptionManager.getSubscribers(message.world, message.cx, message.cz)) {
            subscriber.send(new ServerChangedChunkStatusMessage(message.world, message.cx, message.cz, message.status, connection.getBungeeCordName()));
        }
    }
}
