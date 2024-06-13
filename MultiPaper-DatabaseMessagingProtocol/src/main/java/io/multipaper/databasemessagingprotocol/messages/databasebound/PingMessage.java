package io.multipaper.databasemessagingprotocol.messages.databasebound;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class PingMessage extends DatabaseBoundMessage {

    public PingMessage() {

    }

    public PingMessage(ExtendedByteBuf byteBuf) {

    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {

    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
