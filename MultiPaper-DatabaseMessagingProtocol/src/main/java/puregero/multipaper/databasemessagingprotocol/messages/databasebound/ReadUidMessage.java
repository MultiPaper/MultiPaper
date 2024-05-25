package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class ReadUidMessage extends DatabaseBoundMessage {

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
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
