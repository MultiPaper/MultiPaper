package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class ForceReadChunkMessage extends DatabaseBoundMessage {

    public final String world;
    public final String path;
    public final int cx;
    public final int cz;

    public ForceReadChunkMessage(String world, String path, int cx, int cz) {
        this.world = world;
        this.path = path;
        this.cx = cx;
        this.cz = cz;
    }

    public ForceReadChunkMessage(ExtendedByteBuf byteBuf) {
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
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
