package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

import java.util.UUID;

public class PlayerDisconnectMessage extends MasterBoundMessage {

    public final UUID uuid;

    public PlayerDisconnectMessage(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerDisconnectMessage(ExtendedByteBuf byteBuf) {
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
