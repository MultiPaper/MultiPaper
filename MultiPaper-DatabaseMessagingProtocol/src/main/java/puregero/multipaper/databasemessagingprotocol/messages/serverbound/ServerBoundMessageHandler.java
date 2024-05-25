package puregero.multipaper.databasemessagingprotocol.messages.serverbound;

import puregero.multipaper.databasemessagingprotocol.messages.MessageHandler;
import puregero.multipaper.databasemessagingprotocol.messages.databasebound.DatabaseBoundDataStreamMessage;
import puregero.multipaper.databasemessagingprotocol.messages.databasebound.DatabaseBoundMessage;

public abstract class ServerBoundMessageHandler extends MessageHandler<ServerBoundMessage> {

    @Override
    public DatabaseBoundMessage createDataStreamMessage(int streamId, byte[] data, int offset, int length) {
        return new DatabaseBoundDataStreamMessage(streamId, data, offset, length);
    }

    public abstract void handle(ServerInfoUpdateMessage message);

    public abstract void handle(SetSecretMessage message);

    public abstract void handle(ShutdownMessage message);

    public abstract void handle(ServerChangedChunkStatusMessage message);

    public abstract void handle(FileContentMessage message);

    public abstract void handle(SetChunkOwnerMessage message);

    public abstract void handle(ServerStartedMessage message);

    public abstract void handle(DataUpdateMessage message);

    public abstract void handle(AddChunkSubscriberMessage message);

    public abstract void handle(RemoveChunkSubscriberMessage message);

    public abstract void handle(AddEntitySubscriberMessage message);

    public abstract void handle(RemoveEntitySubscriberMessage message);

    public abstract void handle(ChunkSubscribersSyncMessage message);

    public abstract void handle(EntitySubscribersSyncMessage message);
}
