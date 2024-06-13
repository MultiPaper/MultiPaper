package io.multipaper.databasemessagingprotocol.messages.serverbound;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class VoidMessage extends ServerBoundMessage {

    public VoidMessage() {

    }

    public VoidMessage(ExtendedByteBuf byteBuf) {

    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {

    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
