package io.multipaper.databasemessagingprotocol.messages.serverbound;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class DataMessage extends ServerBoundMessage {

    public final byte[] data;

    public DataMessage(byte[] data) {
        this.data = data;
    }

    public DataMessage(ExtendedByteBuf byteBuf) {
        data = new byte[byteBuf.readVarInt()];
        byteBuf.readBytes(data);
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeVarInt(data.length);
        byteBuf.writeBytes(data);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
