package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

import java.util.UUID;

public class PlayerConnectMessage extends DatabaseBoundMessage {

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
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
