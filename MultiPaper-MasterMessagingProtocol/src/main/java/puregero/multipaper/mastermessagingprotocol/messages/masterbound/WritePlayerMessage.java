package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class WritePlayerMessage extends MasterBoundMessage {

    public final String world;
    public final String uuid;

    public final byte[] data;

    public WritePlayerMessage(String world, String uuid, byte[] data) {
        this.world = world;
        this.uuid = uuid;
        this.data = data;
    }

    public WritePlayerMessage(ExtendedByteBuf byteBuf) {
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
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
