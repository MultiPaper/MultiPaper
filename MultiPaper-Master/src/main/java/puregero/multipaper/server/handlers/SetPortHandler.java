package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.SetPortMessage;
import puregero.multipaper.server.ServerConnection;

public class SetPortHandler {
    public static void handle(ServerConnection connection, SetPortMessage message) {
        connection.setPort(message.port);
    }
}
