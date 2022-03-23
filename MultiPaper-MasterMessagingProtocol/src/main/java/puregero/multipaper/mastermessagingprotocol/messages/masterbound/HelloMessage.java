package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class HelloMessage extends MasterBoundMessage {

    public final String name;

    public HelloMessage(String name) {
        this.name = name;
    }

    public HelloMessage(ExtendedByteBuf byteBuf) {
        name = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(name);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
