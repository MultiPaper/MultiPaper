package puregero.multipaper.mastermessagingprotocol.messages.serverbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class DataUpdateMessage extends ServerBoundMessage {

    public final String path;

    public final byte[] data;

    public DataUpdateMessage(String path, byte[] data) {
        this.path = path;
        this.data = data;
    }

    public DataUpdateMessage(ExtendedByteBuf byteBuf) {
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
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
