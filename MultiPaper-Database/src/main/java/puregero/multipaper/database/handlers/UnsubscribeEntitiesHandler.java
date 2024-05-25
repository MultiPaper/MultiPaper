package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.UnsubscribeEntitiesMessage;
import puregero.multipaper.database.EntitiesSubscriptionManager;
import puregero.multipaper.database.ServerConnection;

public class UnsubscribeEntitiesHandler {
    public static void handle(ServerConnection connection, UnsubscribeEntitiesMessage message) {
        EntitiesSubscriptionManager.unsubscribe(connection, message.world, message.cx, message.cz);
    }
}
