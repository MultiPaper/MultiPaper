package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.PlayerDisconnectMessage;
import puregero.multipaper.database.ServerConnection;

public class PlayerDisconnectHandler {
    public static void handle(ServerConnection connection, PlayerDisconnectMessage message) {
        connection.removePlayer(message.uuid);
    }
}
