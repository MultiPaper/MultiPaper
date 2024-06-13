package io.multipaper.database.handlers;

import io.multipaper.database.ServerConnection;
import io.multipaper.database.ServerInfo;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteTickTimeMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.VoidMessage;

public class WriteTickTimeHandler {
    public static void handle(ServerConnection connection, WriteTickTimeMessage message) {
        ServerInfo serverInfo = connection.getServerInfo();

        serverInfo.getTimer().append(message.tickTime);
        serverInfo.setTps(message.tps);
        
        if (message.tickTime == -1) {
            serverInfo.setTps(-1);
        }

        connection.send(new VoidMessage());
    }
}
