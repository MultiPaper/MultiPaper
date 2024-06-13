package io.multipaper.databasemessagingprotocol.messages.serverbound;

import io.multipaper.databasemessagingprotocol.messages.MessageHandler;

public abstract class ServerBoundMessageHandler extends MessageHandler<ServerBoundMessage> {

    public abstract void handle(ServerBoundMessage message);

}
