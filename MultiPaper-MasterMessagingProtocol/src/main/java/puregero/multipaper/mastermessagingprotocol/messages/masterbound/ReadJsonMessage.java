package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class ReadJsonMessage extends MasterBoundMessage {

    public final String file;

    public ReadJsonMessage(String file) {
        this.file = file;
    }

    public ReadJsonMessage(ExtendedByteBuf byteBuf) {
        file = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(file);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
