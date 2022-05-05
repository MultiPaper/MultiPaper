package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.messages.MessageHandler;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ServerBoundDataStreamMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ServerBoundMessage;

public abstract class MasterBoundMessageHandler extends MessageHandler<MasterBoundMessage> {

    @Override
    public ServerBoundMessage createDataStreamMessage(int streamId, byte[] data, int offset, int length) {
        return new ServerBoundDataStreamMessage(streamId, data, offset, length);
    }

    public abstract void handle(HelloMessage message);

    public abstract void handle(CallDataStorageMessage message);

    public abstract void handle(ChunkChangedStatusMessage message);

    public abstract void handle(DownloadFileMessage message);

    public abstract void handle(ForceReadChunkMessage message);

    public abstract void handle(LockChunkMessage message);

    public abstract void handle(PlayerConnectMessage message);

    public abstract void handle(PlayerDisconnectMessage message);

    public abstract void handle(ReadAdvancementMessage message);

    public abstract void handle(ReadChunkMessage message);

    public abstract void handle(ReadDataMessage message);

    public abstract void handle(ReadJsonMessage message);

    public abstract void handle(ReadLevelMessage message);

    public abstract void handle(ReadPlayerMessage message);

    public abstract void handle(ReadStatsMessage message);

    public abstract void handle(ReadUidMessage message);

    public abstract void handle(RequestChunkOwnershipMessage message);

    public abstract void handle(RequestFilesToSyncMessage message);

    public abstract void handle(SetPortMessage message);

    public abstract void handle(StartMessage message);

    public abstract void handle(SubscribeChunkMessage message);

    public abstract void handle(SubscribeEntitiesMessage message);

    public abstract void handle(SyncChunkOwnerToAllMessage message);

    public abstract void handle(SyncChunkSubscribersMessage message);

    public abstract void handle(SyncEntitiesSubscribersMessage message);

    public abstract void handle(UnlockChunkMessage message);

    public abstract void handle(UnsubscribeChunkMessage message);

    public abstract void handle(UnsubscribeEntitiesMessage message);

    public abstract void handle(UploadFileMessage message);

    public abstract void handle(WillSaveChunkLaterMessage message);

    public abstract void handle(WriteAdvancementsMessage message);

    public abstract void handle(WriteChunkMessage message);

    public abstract void handle(WriteDataMessage message);

    public abstract void handle(WriteJsonMessage message);

    public abstract void handle(WriteLevelMessage message);

    public abstract void handle(WritePlayerMessage message);

    public abstract void handle(WriteStatsMessage message);

    public abstract void handle(WriteTickTimeMessage message);

    public abstract void handle(WriteUidMessage message);
}
