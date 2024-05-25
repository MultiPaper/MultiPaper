package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.SetPortMessage;
import puregero.multipaper.database.ServerConnection;

public class SetPortHandler {
    public static void handle(ServerConnection connection, SetPortMessage message) {
        connection.setPort(message.port);
    }
}
