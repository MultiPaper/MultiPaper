package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class WriteDataMessage extends DatabaseBoundMessage {

    public final String path;
    public final byte[] data;

    public WriteDataMessage(String path, byte[] data) {
        this.path = path;
        this.data = data;
    }

    public WriteDataMessage(ExtendedByteBuf byteBuf) {
        path = byteBuf.readString();
        data = new byte[byteBuf.readVarInt()];
        byteBuf.readBytes(data);
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(path);
        byteBuf.writeVarInt(data.length);
        byteBuf.writeBytes(data);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
