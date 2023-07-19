package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

import java.util.UUID;

public class HelloMessage extends MasterBoundMessage {

    public final String name;

    public final UUID serverUuid;

    public HelloMessage(String name, UUID serverUuid) {
        this.name = name;
        this.serverUuid = serverUuid;
    }

    public HelloMessage(ExtendedByteBuf byteBuf) {
        name = byteBuf.readString();
        serverUuid = byteBuf.readUUID();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(name);
        byteBuf.writeUUID(serverUuid);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
