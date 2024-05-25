package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class UnlockChunkMessage extends DatabaseBoundMessage {

    public final String world;
    public final int cx;
    public final int cz;

    public UnlockChunkMessage(String world, int cx, int cz) {
        this.world = world;
        this.cx = cx;
        this.cz = cz;
    }

    public UnlockChunkMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        cx = byteBuf.readInt();
        cz = byteBuf.readInt();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeInt(cx);
        byteBuf.writeInt(cz);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
