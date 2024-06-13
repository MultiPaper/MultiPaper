package io.multipaper.databasemessagingprotocol.messages.databasebound;

import io.multipaper.databasemessagingprotocol.messages.Protocol;

public class DatabaseBoundProtocol extends Protocol<DatabaseBoundMessage> {

    public DatabaseBoundProtocol() {
        addMessage(HelloMessage.class, HelloMessage::new);
        addMessage(PingMessage.class, PingMessage::new);
        addMessage(ReadAdvancementMessage.class, ReadAdvancementMessage::new);
        addMessage(ReadChunkMessage.class, ReadChunkMessage::new);
        addMessage(ReadDataMessage.class, ReadDataMessage::new);
        addMessage(ReadJsonMessage.class, ReadJsonMessage::new);
        addMessage(ReadLevelMessage.class, ReadLevelMessage::new);
        addMessage(ReadPlayerMessage.class, ReadPlayerMessage::new);
        addMessage(ReadStatsMessage.class, ReadStatsMessage::new);
        addMessage(ReadUidMessage.class, ReadUidMessage::new);
        addMessage(RequestServersListMessage.class, RequestServersListMessage::new);
        addMessage(StartMessage.class, StartMessage::new);
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
