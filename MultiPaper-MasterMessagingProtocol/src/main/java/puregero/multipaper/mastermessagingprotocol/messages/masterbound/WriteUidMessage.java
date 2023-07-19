package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class WriteUidMessage extends MasterBoundMessage {

    public final String world;
    
    public final byte[] data;

    public WriteUidMessage(String world, byte[] data) {
        this.world = world;
        this.data = data;
    }

    public WriteUidMessage(ExtendedByteBuf byteBuf) {
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
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
