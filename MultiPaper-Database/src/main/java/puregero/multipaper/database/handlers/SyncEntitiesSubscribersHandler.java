package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.SyncEntitiesSubscribersMessage;
import puregero.multipaper.database.EntitiesSubscriptionManager;
import puregero.multipaper.database.ServerConnection;

public class SyncEntitiesSubscribersHandler {
    public static void handle(ServerConnection connection, SyncEntitiesSubscribersMessage message) {
        EntitiesSubscriptionManager.syncSubscribers(connection, message.world, message.cx, message.cz);
    }
}
