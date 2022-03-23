package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class ReadChunkMessage extends MasterBoundMessage {

    public final String world;
    public final String path;
    public final int cx;
    public final int cz;

    public ReadChunkMessage(String world, String path, int cx, int cz) {
        this.world = world;
        this.path = path;
        this.cx = cx;
        this.cz = cz;
    }

    public ReadChunkMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        path = byteBuf.readString();
        cx = byteBuf.readInt();
        cz = byteBuf.readInt();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeString(path);
        byteBuf.writeInt(cx);
        byteBuf.writeInt(cz);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
