package io.multipaper.databasemessagingprotocol.messages.databasebound;

import io.multipaper.databasemessagingprotocol.messages.MessageHandler;

public abstract class DatabaseBoundMessageHandler extends MessageHandler<DatabaseBoundMessage> {

    public abstract void handle(HelloMessage message);

    public abstract void handle(PingMessage pingMessage);

    public abstract void handle(ReadAdvancementMessage message);

    public abstract void handle(ReadChunkMessage message);

    public abstract void handle(ReadDataMessage message);

    public abstract void handle(ReadJsonMessage message);

    public abstract void handle(ReadLevelMessage message);

    public abstract void handle(ReadPlayerMessage message);

    public abstract void handle(ReadStatsMessage message);

    public abstract void handle(ReadUidMessage message);

    public abstract void handle(RequestServersListMessage message);

    public abstract void handle(StartMessage message);

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
