package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.WillSaveEntitiesLaterMessage;
import puregero.multipaper.server.EntitiesLockManager;
import puregero.multipaper.server.ServerConnection;

public class WillSaveEntitiesHandler {
    public static void handle(ServerConnection connection, WillSaveEntitiesLaterMessage message) {
        EntitiesLockManager.lockUntilWrite(message.world, message.cx, message.cz);
    }
}
