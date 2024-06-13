package io.multipaper.databasemessagingprotocol.messages.databasebound;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class ReadLevelMessage extends DatabaseBoundMessage {

    public final String world;

    public ReadLevelMessage(String world) {
        this.world = world;
    }

    public ReadLevelMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
