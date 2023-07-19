package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.SubscribeEntitiesMessage;
import puregero.multipaper.server.EntitiesSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

public class SubscribeEntitiesHandler {

    public static void handle(ServerConnection connection, SubscribeEntitiesMessage message) {
        EntitiesSubscriptionManager.subscribe(connection, message.world, message.cx, message.cz);
    }
}
