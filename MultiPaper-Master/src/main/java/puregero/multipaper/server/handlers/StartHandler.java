package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.StartMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ServerStartedMessage;
import puregero.multipaper.server.ServerConnection;

public class StartHandler {

    public static void handle(ServerConnection connection, StartMessage message) {
        if (!message.host.isEmpty() && !message.host.equals("0.0.0.0")) {
            System.out.print("Setting " + connection.getBungeeCordName() + "'s host to " + message.host + " with port " + message.port);
            if (!message.host.equals(connection.getHost())) {
                System.out.print(" (Host address was " + connection.getHost() + ")");
            }
            System.out.println();
            connection.setHost(message.host);
        }

        System.out.println(connection.getBungeeCordName() + " (" + connection.getHost() + ":" + message.port + ") has started, broadcasting start to other servers...");
        connection.broadcastOthers(new ServerStartedMessage(connection.getHost(), message.port));
    }
}
