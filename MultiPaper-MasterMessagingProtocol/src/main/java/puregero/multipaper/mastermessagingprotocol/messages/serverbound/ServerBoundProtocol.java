package puregero.multipaper.mastermessagingprotocol.messages.serverbound;

import puregero.multipaper.mastermessagingprotocol.messages.Protocol;

public class ServerBoundProtocol extends Protocol<ServerBoundMessage> {

    public ServerBoundProtocol() {
        addMessage(ServerInfoUpdateMessage.class, ServerInfoUpdateMessage::new);
        addMessage(SetSecretMessage.class, SetSecretMessage::new);
        addMessage(ShutdownMessage.class, ShutdownMessage::new);
        addMessage(ServerChangedChunkStatusMessage.class, ServerChangedChunkStatusMessage::new);
        addMessage(FileContentMessage.class, FileContentMessage::new);
        addMessage(DataMessageReply.class, DataMessageReply::new);
        addMessage(SetChunkOwnerMessage.class, SetChunkOwnerMessage::new);
        addMessage(BooleanMessageReply.class, BooleanMessageReply::new);
        addMessage(ChunkLoadedOnAnotherServerMessage.class, ChunkLoadedOnAnotherServerMessage::new);
        addMessage(FilesToSyncMessage.class, FilesToSyncMessage::new);
        addMessage(ServerStartedMessage.class, ServerStartedMessage::new);
        addMessage(DataUpdateMessage.class, DataUpdateMessage::new);
        addMessage(AddChunkSubscriberMessage.class, AddChunkSubscriberMessage::new);
        addMessage(AddEntitySubscriberMessage.class, AddEntitySubscriberMessage::new);
        addMessage(RemoveChunkSubscriberMessage.class, RemoveChunkSubscriberMessage::new);
        addMessage(RemoveEntitySubscriberMessage.class, RemoveEntitySubscriberMessage::new);
        addMessage(ChunkSubscribersSyncMessage.class, ChunkSubscribersSyncMessage::new);
        addMessage(EntitySubscribersSyncMessage.class, EntitySubscribersSyncMessage::new);
    }

}
