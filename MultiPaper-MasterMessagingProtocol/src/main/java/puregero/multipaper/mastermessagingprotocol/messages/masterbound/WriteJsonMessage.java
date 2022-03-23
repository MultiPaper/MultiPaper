package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class WriteJsonMessage extends MasterBoundMessage {

    public final String file;
    public final byte[] data;

    public WriteJsonMessage(String file, byte[] data) {
        this.file = file;
        this.data = data;
    }

    public WriteJsonMessage(ExtendedByteBuf byteBuf) {
        file = byteBuf.readString();
        data = new byte[byteBuf.readVarInt()];
        byteBuf.readBytes(data);
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(file);
        byteBuf.writeVarInt(data.length);
        byteBuf.writeBytes(data);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
