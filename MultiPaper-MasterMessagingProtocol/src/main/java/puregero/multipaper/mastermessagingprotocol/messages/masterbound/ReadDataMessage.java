package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class ReadDataMessage extends MasterBoundMessage {

    public final String path;

    public ReadDataMessage(String path) {
        this.path = path;
    }

    public ReadDataMessage(ExtendedByteBuf byteBuf) {
        path = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(path);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
