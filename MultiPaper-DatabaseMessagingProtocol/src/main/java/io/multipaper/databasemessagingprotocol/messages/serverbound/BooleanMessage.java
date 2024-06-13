package io.multipaper.databasemessagingprotocol.messages.serverbound;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class BooleanMessage extends ServerBoundMessage {

    public final boolean result;

    public BooleanMessage(boolean result) {
        this.result = result;
    }

    public BooleanMessage(ExtendedByteBuf byteBuf) {
        this.result = byteBuf.readBoolean();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeBoolean(this.result);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
