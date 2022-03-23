package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class WriteChunkMessage extends MasterBoundMessage {

    public final String world;
    public final String path;
    public final int cx;
    public final int cz;
    public final byte[] data;

    public WriteChunkMessage(String world, String path, int cx, int cz, byte[] data) {
        this.world = world;
        this.path = path;
        this.cx = cx;
        this.cz = cz;
        this.data = data;
    }

    public WriteChunkMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        path = byteBuf.readString();
        cx = byteBuf.readInt();
        cz = byteBuf.readInt();
        data = new byte[byteBuf.readVarInt()];
        byteBuf.readBytes(data);
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeString(path);
        byteBuf.writeInt(cx);
        byteBuf.writeInt(cz);
        byteBuf.writeVarInt(data.length);
        byteBuf.writeBytes(data);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
