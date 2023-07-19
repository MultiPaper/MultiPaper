package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.UnsubscribeChunkMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

public class UnsubscribeChunkHandler {

    public static void handle(ServerConnection connection, UnsubscribeChunkMessage message) {
        ChunkSubscriptionManager.unsubscribe(connection, message.world, message.cx, message.cz);

        connection.sendReply(new BooleanMessageReply(true), message);
    }
}
