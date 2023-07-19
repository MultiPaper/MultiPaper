package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.UnsubscribeEntitiesMessage;
import puregero.multipaper.server.EntitiesSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

public class UnsubscribeEntitiesHandler {

    public static void handle(ServerConnection connection, UnsubscribeEntitiesMessage message) {
        EntitiesSubscriptionManager.unsubscribe(connection, message.world, message.cx, message.cz);
    }
}
