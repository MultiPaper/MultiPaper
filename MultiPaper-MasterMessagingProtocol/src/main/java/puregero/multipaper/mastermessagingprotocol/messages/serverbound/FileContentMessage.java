package puregero.multipaper.mastermessagingprotocol.messages.serverbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class FileContentMessage extends ServerBoundMessage {

    public final String path;
    public final long lastModified;
    public final int streamId;

    public FileContentMessage(String path, long lastModified, int streamId) {
        this.path = path;
        this.lastModified = lastModified;
        this.streamId = streamId;
    }

    public FileContentMessage(ExtendedByteBuf byteBuf) {
        path = byteBuf.readString();
        lastModified = byteBuf.readLong();
        streamId = byteBuf.readVarInt();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(path);
        byteBuf.writeLong(lastModified);
        byteBuf.writeVarInt(streamId);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
