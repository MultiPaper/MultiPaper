package io.multipaper.databasemessagingprotocol.messages.databasebound;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class ReadDataMessage extends DatabaseBoundMessage {

    public final String path;

    public ReadDataMessage(String path) {
        this.path = path;
    }

    public ReadDataMessage(ExtendedByteBuf byteBuf) {
        path = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(path);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
