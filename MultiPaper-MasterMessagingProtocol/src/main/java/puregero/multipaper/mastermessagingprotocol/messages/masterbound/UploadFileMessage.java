package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class UploadFileMessage extends MasterBoundMessage {

    public final boolean immediatelySyncToOtherServers;
    public final String path;
    public final long lastModified;
    public final byte[] data;

    public UploadFileMessage(boolean immediatelySyncToOtherServers, String path, long lastModified, byte[] data) {
        this.immediatelySyncToOtherServers = immediatelySyncToOtherServers;
        this.path = path;
        this.lastModified = lastModified;
        this.data = data;
    }

    public UploadFileMessage(ExtendedByteBuf byteBuf) {
        immediatelySyncToOtherServers = byteBuf.readBoolean();
        path = byteBuf.readString();
        lastModified = byteBuf.readLong();
        data = new byte[byteBuf.readVarInt()];
        byteBuf.readBytes(data);
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeBoolean(immediatelySyncToOtherServers);
        byteBuf.writeString(path);
        byteBuf.writeLong(lastModified);
        byteBuf.writeVarInt(data.length);
        byteBuf.writeBytes(data);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
