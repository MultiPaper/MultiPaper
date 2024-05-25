package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.SubscribeEntitiesMessage;
import puregero.multipaper.database.EntitiesSubscriptionManager;
import puregero.multipaper.database.ServerConnection;

public class SubscribeEntitiesHandler {
    public static void handle(ServerConnection connection, SubscribeEntitiesMessage message) {
        EntitiesSubscriptionManager.subscribe(connection, message.world, message.cx, message.cz);
    }
}
