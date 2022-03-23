package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.PlayerConnectMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.server.ServerConnection;

import java.util.List;

public class PlayerConnectHandler {
    public static void handle(ServerConnection connection, PlayerConnectMessage message) {
        List<ServerConnection> connections = ServerConnection.getConnections();

        synchronized (connections) {
            for (ServerConnection otherConnection : connections) {
                if (otherConnection != connection && otherConnection.hasPlayer(message.uuid)) {
                    connection.sendReply(new BooleanMessageReply(false), message);
                }
            }

            connection.addPlayer(message.uuid);
        }

        connection.sendReply(new BooleanMessageReply(true), message);
    }
}
