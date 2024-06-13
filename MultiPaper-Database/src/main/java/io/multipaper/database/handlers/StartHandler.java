package io.multipaper.database.handlers;

import io.multipaper.database.MultiPaperDatabase;
import io.multipaper.database.ServerConnection;
import io.multipaper.database.ServerInfo;
import io.multipaper.databasemessagingprotocol.messages.databasebound.StartMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.SetSecretMessage;

public class StartHandler {
    public static void handle(ServerConnection connection, StartMessage message) {
        ServerInfo serverInfo = connection.getServerInfo();
        if (!message.host.isEmpty() && !message.host.equals("0.0.0.0")) {
            System.out.print("Setting " + serverInfo.getName() + "'s host to " + message.host + " with port " + message.port);
            if (!message.host.equals(serverInfo.getHost())) {
                System.out.print(" (Host address was " + serverInfo.getHost() + ")");
            }
            System.out.println();
            serverInfo.setHost(message.host);
        }

        serverInfo.setPort(message.port);

        System.out.println(serverInfo.getName() + " (" + serverInfo.getHost() + ":" + message.port + ") has started.");
        connection.send(new SetSecretMessage(MultiPaperDatabase.SECRET));
    }
}
