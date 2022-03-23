package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class ReadAdvancementMessage extends MasterBoundMessage {

    public final String world;
    public final String uuid;

    public ReadAdvancementMessage(String world, String uuid) {
        this.world = world;
        this.uuid = uuid;
    }

    public ReadAdvancementMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        uuid = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeString(uuid);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
