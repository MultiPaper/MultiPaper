package io.multipaper.database.handlers;

import io.multipaper.database.ServerConnection;
import io.multipaper.database.ServerInfo;
import io.multipaper.databasemessagingprotocol.messages.databasebound.RequestServersListMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.ServerInfoListMessage;

import java.util.List;

public class RequestServersListHandler {
    public static void handle(ServerConnection connection, RequestServersListMessage message) {
        List<ServerInfoListMessage.Server> servers = ServerInfo.getServers().stream()
                .filter(serverInfo -> serverInfo.getLastMessageTime() > System.currentTimeMillis() - 15 * 60 * 1000)
                .map(serverInfo -> new ServerInfoListMessage.Server(serverInfo.getName(), serverInfo.getUuid(), (int) serverInfo.getTimer().averageInMillis(), (float) serverInfo.getTps(), serverInfo.isAlive()))
                .toList();
        connection.send(new ServerInfoListMessage(servers));
    }
}
