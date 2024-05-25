package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

import java.util.UUID;

public class PlayerDisconnectMessage extends DatabaseBoundMessage {

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
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
