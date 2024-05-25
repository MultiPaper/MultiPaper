package puregero.multipaper.databasemessagingprotocol.messages.serverbound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class ServerChangedChunkStatusMessage extends ServerBoundMessage {

    public final String world;
    public final int cx;
    public final int cz;
    public final String status;
    public final String server;

    public ServerChangedChunkStatusMessage(String world, int cx, int cz, String status, String server) {
        this.world = world;
        this.cx = cx;
        this.cz = cz;
        this.status = status;
        this.server = server;
    }

    public ServerChangedChunkStatusMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        cx = byteBuf.readInt();
        cz = byteBuf.readInt();
        status = byteBuf.readString();
        server = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeInt(cx);
        byteBuf.writeInt(cz);
        byteBuf.writeString(status);
        byteBuf.writeString(server);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
