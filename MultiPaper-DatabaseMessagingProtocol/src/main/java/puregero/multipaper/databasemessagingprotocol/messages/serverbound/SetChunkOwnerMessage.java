package puregero.multipaper.databasemessagingprotocol.messages.serverbound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class SetChunkOwnerMessage extends ServerBoundMessage {

    public final String world;
    public final int cx;
    public final int cz;
    public final String owner;

    public SetChunkOwnerMessage(String world, int cx, int cz, String owner) {
        this.world = world;
        this.cx = cx;
        this.cz = cz;
        this.owner = owner;
    }

    public SetChunkOwnerMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        cx = byteBuf.readInt();
        cz = byteBuf.readInt();
        owner = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeInt(cx);
        byteBuf.writeInt(cz);
        byteBuf.writeString(owner);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
