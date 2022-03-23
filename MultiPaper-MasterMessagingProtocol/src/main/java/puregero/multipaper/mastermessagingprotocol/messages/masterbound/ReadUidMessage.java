package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class ReadUidMessage extends MasterBoundMessage {

    public final String world;

    public ReadUidMessage(String world) {
        this.world = world;
    }

    public ReadUidMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
