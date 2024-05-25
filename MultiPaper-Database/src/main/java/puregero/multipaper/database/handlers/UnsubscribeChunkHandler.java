package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.UnsubscribeChunkMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.database.ChunkSubscriptionManager;
import puregero.multipaper.database.ServerConnection;

public class UnsubscribeChunkHandler {
    public static void handle(ServerConnection connection, UnsubscribeChunkMessage message) {
        ChunkSubscriptionManager.unsubscribe(connection, message.world, message.cx, message.cz);

        connection.sendReply(new BooleanMessageReply(true), message);
    }
}
