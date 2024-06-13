package io.multipaper.databasemessagingprotocol.messages.databasebound;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

import java.util.UUID;

public class HelloMessage extends DatabaseBoundMessage {

    public final String name;
    public final UUID serverUuid;
    public final long inboundProtocolHash;
    public final long outboundProtocolHash;

    public HelloMessage(String name, UUID serverUuid, long inboundProtocolHash, long outboundProtocolHash) {
        this.name = name;
        this.serverUuid = serverUuid;
        this.inboundProtocolHash = inboundProtocolHash;
        this.outboundProtocolHash = outboundProtocolHash;
    }

    public HelloMessage(ExtendedByteBuf byteBuf) {
        this.name = byteBuf.readString();
        this.serverUuid = byteBuf.readUUID();
        this.inboundProtocolHash = byteBuf.readLong();
        this.outboundProtocolHash = byteBuf.readLong();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(this.name);
        byteBuf.writeUUID(this.serverUuid);
        byteBuf.writeLong(this.inboundProtocolHash);
        byteBuf.writeLong(this.outboundProtocolHash);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
