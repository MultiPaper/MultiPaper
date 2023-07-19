package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.SyncEntitiesSubscribersMessage;
import puregero.multipaper.server.EntitiesSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

public class SyncEntitiesSubscribersHandler {

    public static void handle(ServerConnection connection, SyncEntitiesSubscribersMessage message) {
        EntitiesSubscriptionManager.syncSubscribers(connection, message.world, message.cx, message.cz);
    }
}
