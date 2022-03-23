package puregero.multipaper.mastermessagingprotocol.messages.serverbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class RemoveChunkSubscriberMessage extends ServerBoundMessage {

    public final String world;
    public final int cx;
    public final int cz;
    public final String server;

    public RemoveChunkSubscriberMessage(String world, int cx, int cz, String server) {
        this.world = world;
        this.cx = cx;
        this.cz = cz;
        this.server = server;
    }

    public RemoveChunkSubscriberMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        cx = byteBuf.readInt();
        cz = byteBuf.readInt();
        server = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeInt(cx);
        byteBuf.writeInt(cz);
        byteBuf.writeString(server);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
