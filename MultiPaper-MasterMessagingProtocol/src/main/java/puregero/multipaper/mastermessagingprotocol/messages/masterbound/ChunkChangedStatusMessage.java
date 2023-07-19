package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class ChunkChangedStatusMessage extends MasterBoundMessage {

    public final String world;

    public final int cx;
    public final int cz;

    public final String status;

    public ChunkChangedStatusMessage(String world, int cx, int cz, String status) {
        this.world = world;
        this.cx = cx;
        this.cz = cz;
        this.status = status;
    }

    public ChunkChangedStatusMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        cx = byteBuf.readInt();
        cz = byteBuf.readInt();
        status = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeInt(cx);
        byteBuf.writeInt(cz);
        byteBuf.writeString(status);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
