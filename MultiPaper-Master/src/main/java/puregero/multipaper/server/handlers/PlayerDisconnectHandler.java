package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.PlayerDisconnectMessage;
import puregero.multipaper.server.ServerConnection;

public class PlayerDisconnectHandler {
    public static void handle(ServerConnection connection, PlayerDisconnectMessage message) {
        connection.removePlayer(message.uuid);
    }
}
