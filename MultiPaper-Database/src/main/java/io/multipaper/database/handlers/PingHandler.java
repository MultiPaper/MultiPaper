package io.multipaper.database.handlers;

import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.PingMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.BooleanMessage;

public class PingHandler {
    public static void handle(ServerConnection connection, PingMessage message) {
        AsyncIO.runAsync(() -> {
            // Run it as an async task to ensure the async system is functioning
            connection.send(new BooleanMessage(true));
        });
    }
}
