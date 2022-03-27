package puregero.multipaper.mastermessagingprotocol.messages.serverbound;

import puregero.multipaper.mastermessagingprotocol.messages.MessageHandler;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.MasterBoundDataStreamMessage;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.MasterBoundMessage;

public abstract class ServerBoundMessageHandler extends MessageHandler<ServerBoundMessage> {

    @Override
    public MasterBoundMessage createDataStreamMessage(int streamId, byte[] data, int offset, int length) {
        return new MasterBoundDataStreamMessage(streamId, data, offset, length);
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
