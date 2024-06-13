package io.multipaper.database;

import io.multipaper.database.handlers.PingHandler;
import io.multipaper.database.handlers.ReadAdvancementsHandler;
import io.multipaper.database.handlers.ReadChunkHandler;
import io.multipaper.database.handlers.ReadDataHandler;
import io.multipaper.database.handlers.ReadJsonHandler;
import io.multipaper.database.handlers.ReadLevelHandler;
import io.multipaper.database.handlers.ReadPlayerHandler;
import io.multipaper.database.handlers.ReadStatsHandler;
import io.multipaper.database.handlers.ReadUidHandler;
import io.multipaper.database.handlers.RequestServersListHandler;
import io.multipaper.database.handlers.StartHandler;
import io.multipaper.database.handlers.WriteAdvancementsHandler;
import io.multipaper.database.handlers.WriteChunkHandler;
import io.multipaper.database.handlers.WriteDataHandler;
import io.multipaper.database.handlers.WriteJsonHandler;
import io.multipaper.database.handlers.WriteLevelHandler;
import io.multipaper.database.handlers.WritePlayerHandler;
import io.multipaper.database.handlers.WriteStatsHandler;
import io.multipaper.database.handlers.WriteTickTimeHandler;
import io.multipaper.database.handlers.WriteUidHandler;
import io.multipaper.databasemessagingprotocol.messages.databasebound.DatabaseBoundMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.DatabaseBoundMessageHandler;
import io.multipaper.databasemessagingprotocol.messages.databasebound.HelloMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.PingMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadAdvancementMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadChunkMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadDataMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadJsonMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadLevelMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadPlayerMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadStatsMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadUidMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.RequestServersListMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.StartMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteAdvancementsMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteChunkMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteDataMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteJsonMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteLevelMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WritePlayerMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteStatsMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteTickTimeMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteUidMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.multipaper.databasemessagingprotocol.messages.serverbound.ServerBoundMessage;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerConnection extends DatabaseBoundMessageHandler {
    private final SocketChannel channel;
    private final long inboundProtocolHash;
    private final long outboundProtocolHash;

    private ServerInfo serverInfo;

    public static final Set<ServerConnection> connections = ConcurrentHashMap.newKeySet();

    public ServerConnection(SocketChannel channel, long inboundProtocolHash, long outboundProtocolHash) {
        this.channel = channel;
        this.inboundProtocolHash = inboundProtocolHash;
        this.outboundProtocolHash = outboundProtocolHash;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void send(ServerBoundMessage message) {
        this.channel.writeAndFlush(message);
    }

    @Override
    public void handle(HelloMessage message) {
        this.serverInfo = ServerInfo.getOrCreate(message.serverUuid, message.name);

        if (message.inboundProtocolHash != this.outboundProtocolHash || message.outboundProtocolHash != this.inboundProtocolHash) {
            System.out.println("Refusing connection due to protocol mismatch: " + this.getAddress() + " (" + message.name + " " + message.serverUuid + ")");
            this.channel.close();
            return;
        }

        if (this.serverInfo.getHost() == null) {
            this.serverInfo.setHost(((InetSocketAddress) this.getAddress()).getAddress().getHostAddress());
        }

        connections.add(this);

        System.out.println("Connection from " + this.getAddress() + " (" + message.name + " " + message.serverUuid + ")");
    }

    @Override
    public boolean onMessage(DatabaseBoundMessage message) {
        if (this.serverInfo != null) {
            this.serverInfo.setLastMessageTime(System.currentTimeMillis());
        }

        return false;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        connections.remove(this);

        System.out.println(ctx.channel().remoteAddress() + " (" + this.serverInfo.getName() + " " + this.serverInfo.getUuid() + ") closed");
    }

    public SocketAddress getAddress() {
        return this.channel.remoteAddress();
    }

    public ServerInfo getServerInfo() {
        return this.serverInfo;
    }

    @Override
    public void handle(PingMessage message) {
        PingHandler.handle(this, message);
    }

    @Override
    public void handle(ReadAdvancementMessage message) {
        ReadAdvancementsHandler.handle(this, message);
    }

    @Override
    public void handle(ReadChunkMessage message) {
        ReadChunkHandler.handle(this, message);
    }

    @Override
    public void handle(ReadDataMessage message) {
        ReadDataHandler.handle(this, message);
    }

    @Override
    public void handle(ReadJsonMessage message) {
        ReadJsonHandler.handle(this, message);
    }

    @Override
    public void handle(ReadLevelMessage message) {
        ReadLevelHandler.handle(this, message);
    }

    @Override
    public void handle(ReadPlayerMessage message) {
        ReadPlayerHandler.handle(this, message);
    }

    @Override
    public void handle(ReadStatsMessage message) {
        ReadStatsHandler.handle(this, message);
    }

    @Override
    public void handle(ReadUidMessage message) {
        ReadUidHandler.handle(this, message);
    }

    @Override
    public void handle(RequestServersListMessage message) {
        RequestServersListHandler.handle(this, message);
    }

    @Override
    public void handle(StartMessage message) {
        StartHandler.handle(this, message);
    }

    @Override
    public void handle(WriteAdvancementsMessage message) {
        WriteAdvancementsHandler.handle(this, message);
    }

    @Override
    public void handle(WriteChunkMessage message) {
        WriteChunkHandler.handle(this, message);
    }

    @Override
    public void handle(WriteDataMessage message) {
        WriteDataHandler.handle(this, message);
    }

    @Override
    public void handle(WriteJsonMessage message) {
        WriteJsonHandler.handle(this, message);
    }

    @Override
    public void handle(WriteLevelMessage message) {
        WriteLevelHandler.handle(this, message);
    }

    @Override
    public void handle(WritePlayerMessage message) {
        WritePlayerHandler.handle(this, message);
    }

    @Override
    public void handle(WriteStatsMessage message) {
        WriteStatsHandler.handle(this, message);
    }

    @Override
    public void handle(WriteTickTimeMessage message) {
        WriteTickTimeHandler.handle(this, message);
    }

    @Override
    public void handle(WriteUidMessage message) {
        WriteUidHandler.handle(this, message);
    }
}
