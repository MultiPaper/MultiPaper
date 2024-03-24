package puregero.multipaper.server.handlers;

import lombok.extern.slf4j.Slf4j;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.StartMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ServerStartedMessage;
import puregero.multipaper.server.ServerConnection;

@Slf4j
public class StartHandler {
    public static void handle(ServerConnection connection, StartMessage message) {
        if (!message.host.isEmpty() && !message.host.equals("0.0.0.0")) {
            log.info("Setting " + connection.getBungeeCordName() + "'s host to " + message.host + " with port " + message.port);
            if (!message.host.equals(connection.getHost())) {
                log.info(" (Host address was " + connection.getHost() + ")");
            }

            connection.setHost(message.host);
        }

        log.info(connection.getBungeeCordName() + " (" + connection.getHost() + ":" + message.port + ") has started, broadcasting start to other servers...");
        connection.broadcastOthers(new ServerStartedMessage(connection.getHost(), message.port));
    }
}
