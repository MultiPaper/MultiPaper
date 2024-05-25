package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.PlayerConnectMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.database.ServerConnection;

import java.util.List;

public class PlayerConnectHandler {
    public static void handle(ServerConnection connection, PlayerConnectMessage message) {
        List<ServerConnection> connections = ServerConnection.getConnections();

        synchronized (connections) {
            for (ServerConnection otherConnection : connections) {
                if (otherConnection != connection && otherConnection.hasPlayer(message.uuid)) {
                    connection.sendReply(new BooleanMessageReply(false), message);
                    return;
                }
            }

            connection.addPlayer(message.uuid);
        }

        connection.sendReply(new BooleanMessageReply(true), message);
    }
}
