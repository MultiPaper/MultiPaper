package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.WillSaveEntitiesLaterMessage;
import puregero.multipaper.database.EntitiesLockManager;
import puregero.multipaper.database.ServerConnection;

public class WillSaveEntitiesHandler {
    public static void handle(ServerConnection connection, WillSaveEntitiesLaterMessage message) {
        EntitiesLockManager.lockUntilWrite(message.world, message.cx, message.cz);
    }
}
