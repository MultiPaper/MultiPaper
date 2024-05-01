package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.messages.Protocol;

public class MasterBoundProtocol extends Protocol<MasterBoundMessage> {

    public MasterBoundProtocol() {
        addMessage(MasterBoundDataStreamMessage.class, MasterBoundDataStreamMessage::new);
        addMessage(CallDataStorageMessage.class, CallDataStorageMessage::new);
        addMessage(ChunkChangedStatusMessage.class, ChunkChangedStatusMessage::new);
        addMessage(DownloadFileMessage.class, DownloadFileMessage::new);
        addMessage(ForceReadChunkMessage.class, ForceReadChunkMessage::new);
        addMessage(HelloMessage.class, HelloMessage::new);
        addMessage(PingMessage.class, PingMessage::new);
        addMessage(LockChunkMessage.class, LockChunkMessage::new);
        addMessage(PlayerConnectMessage.class, PlayerConnectMessage::new);
        addMessage(PlayerDisconnectMessage.class, PlayerDisconnectMessage::new);
        addMessage(ReadAdvancementMessage.class, ReadAdvancementMessage::new);
        addMessage(ReadChunkMessage.class, ReadChunkMessage::new);
        addMessage(ReadDataMessage.class, ReadDataMessage::new);
        addMessage(ReadJsonMessage.class, ReadJsonMessage::new);
        addMessage(ReadLevelMessage.class, ReadLevelMessage::new);
        addMessage(ReadPlayerMessage.class, ReadPlayerMessage::new);
        addMessage(ReadStatsMessage.class, ReadStatsMessage::new);
        addMessage(ReadUidMessage.class, ReadUidMessage::new);
        addMessage(RequestChunkOwnershipMessage.class, RequestChunkOwnershipMessage::new);
        addMessage(RequestEntityIdBlock.class, RequestEntityIdBlock::new);
        addMessage(RequestFilesToSyncMessage.class, RequestFilesToSyncMessage::new);
        addMessage(SetPortMessage.class, SetPortMessage::new);
        addMessage(StartMessage.class, StartMessage::new);
        addMessage(SubscribeChunkMessage.class, SubscribeChunkMessage::new);
        addMessage(SubscribeEntitiesMessage.class, SubscribeEntitiesMessage::new);
        addMessage(SyncChunkOwnerToAllMessage.class, SyncChunkOwnerToAllMessage::new);
        addMessage(SyncChunkSubscribersMessage.class, SyncChunkSubscribersMessage::new);
        addMessage(SyncEntitiesSubscribersMessage.class, SyncEntitiesSubscribersMessage::new);
        addMessage(UnlockChunkMessage.class, UnlockChunkMessage::new);
        addMessage(UnsubscribeChunkMessage.class, UnsubscribeChunkMessage::new);
        addMessage(UnsubscribeEntitiesMessage.class, UnsubscribeEntitiesMessage::new);
        addMessage(UploadFileMessage.class, UploadFileMessage::new);
        addMessage(WillSaveChunkLaterMessage.class, WillSaveChunkLaterMessage::new);
        addMessage(WillSaveEntitiesLaterMessage.class, WillSaveEntitiesLaterMessage::new);
        addMessage(WriteAdvancementsMessage.class, WriteAdvancementsMessage::new);
        addMessage(WriteChunkMessage.class, WriteChunkMessage::new);
        addMessage(WriteDataMessage.class, WriteDataMessage::new);
        addMessage(WriteJsonMessage.class, WriteJsonMessage::new);
        addMessage(WriteLevelMessage.class, WriteLevelMessage::new);
        addMessage(WritePlayerMessage.class, WritePlayerMessage::new);
        addMessage(WriteStatsMessage.class, WriteStatsMessage::new);
        addMessage(WriteTickTimeMessage.class, WriteTickTimeMessage::new);
        addMessage(WriteUidMessage.class, WriteUidMessage::new);
    }

}
