package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class WriteLevelMessage extends DatabaseBoundMessage {

    public final String world;
    public final byte[] data;

    public WriteLevelMessage(String world, byte[] data) {
        this.world = world;
        this.data = data;
    }

    public WriteLevelMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        data = new byte[byteBuf.readVarInt()];
        byteBuf.readBytes(data);
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeVarInt(data.length);
        byteBuf.writeBytes(data);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
