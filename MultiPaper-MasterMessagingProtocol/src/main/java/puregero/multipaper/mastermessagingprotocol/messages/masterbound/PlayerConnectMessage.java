package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

import java.util.UUID;

public class PlayerConnectMessage extends MasterBoundMessage {

    public final UUID uuid;

    public PlayerConnectMessage(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerConnectMessage(ExtendedByteBuf byteBuf) {
        uuid = byteBuf.readUUID();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeUUID(uuid);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
