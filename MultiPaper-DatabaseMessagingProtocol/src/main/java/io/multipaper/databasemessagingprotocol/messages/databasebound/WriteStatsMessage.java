package io.multipaper.databasemessagingprotocol.messages.databasebound;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class WriteStatsMessage extends DatabaseBoundMessage {

    public final String world;
    public final String uuid;
    public final byte[] data;

    public WriteStatsMessage(String world, String uuid, byte[] data) {
        this.world = world;
        this.uuid = uuid;
        this.data = data;
    }

    public WriteStatsMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        uuid = byteBuf.readString();
        data = new byte[byteBuf.readVarInt()];
        byteBuf.readBytes(data);
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeString(uuid);
        byteBuf.writeVarInt(data.length);
        byteBuf.writeBytes(data);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
