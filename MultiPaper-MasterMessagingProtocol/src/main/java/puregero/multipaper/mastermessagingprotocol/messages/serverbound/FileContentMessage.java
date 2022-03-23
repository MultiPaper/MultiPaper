package puregero.multipaper.mastermessagingprotocol.messages.serverbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class FileContentMessage extends ServerBoundMessage {

    public final String path;
    public final long lastModified;
    public final byte[] data;

    public FileContentMessage(String path, long lastModified, byte[] data) {
        this.path = path;
        this.lastModified = lastModified;
        this.data = data;
    }

    public FileContentMessage(ExtendedByteBuf byteBuf) {
        path = byteBuf.readString();
        lastModified = byteBuf.readLong();
        data = new byte[byteBuf.readVarInt()];
        byteBuf.readBytes(data);
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(path);
        byteBuf.writeLong(lastModified);
        byteBuf.writeVarInt(data.length);
        byteBuf.writeBytes(data);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
