package puregero.multipaper.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.*;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ServerBoundMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.SetSecretMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ShutdownMessage;
import puregero.multipaper.server.handlers.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ServerConnection extends MasterBoundMessageHandler {
    private final SocketChannel channel;

    private String name;
    private long lastPing = System.currentTimeMillis();
    private final CircularTimer timer = new CircularTimer();
    public final HashSet<UUID> playerUUIDs = new HashSet<>();
    private final List<Player> players = new ArrayList<>();
    private double tps;
    private int port = -1;
    private String host;
    private UUID uuid;

    /**
     * This connection map may include dead servers! Check if a server is alive
     * with `connections` before trying to send any data!
     */
    private static final Map<String, ServerConnection> connectionMap = new ConcurrentHashMap<>();
    private static final List<ServerConnection> connections = new CopyOnWriteArrayList<>();

    public static void shutdown() {
        broadcastAll(new ShutdownMessage());
    }

    public static void shutdownAndWait() {
        while (!connections.isEmpty()) {
            shutdown();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ServerConnection(SocketChannel channel) {
        this.channel = channel;
    }

    public static boolean isAlive(String bungeecordName) {
        return connections.contains(getConnection(bungeecordName)) && getConnection(bungeecordName).isOnline();
    }

    public static ServerConnection getConnection(String bungeecordName) {
        return connectionMap.get(bungeecordName);
    }

    public static List<ServerConnection> getConnections() {
        return connections;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void send(ServerBoundMessage message) {
       channel.writeAndFlush(message);
    }

    public void send(ServerBoundMessage message, Consumer<MasterBoundMessage> callback) {
        send(setCallback(message, callback));
    }

    public void sendReply(ServerBoundMessage message, MasterBoundMessage inReplyTo) {
        message.setTransactionId(inReplyTo.getTransactionId());
        send(message);
    }

    public static void broadcastAll(ServerBoundMessage message) {
        connections.forEach(connection -> connection.send(message));
    }

    public void broadcastOthers(ServerBoundMessage message) {
        connections.forEach(connection -> {
            if (connection != ServerConnection.this) {
                connection.send(message);
            }
        });
    }

    public boolean isOnline() {
        return lastPing > System.currentTimeMillis() - 5000 && tps > 0;
    }

    @Override
    public void handle(HelloMessage message) {
        name = message.name;
        host = ((InetSocketAddress) getAddress()).getAddress().getHostAddress();
        uuid = message.serverUuid;

        synchronized (connections) {
            connections.add(this);
            ServerConnection oldConnectionWithSameName = connectionMap.put(name, this);

            if ("false".equalsIgnoreCase(System.getProperty("allow.multiple.connections", "false"))
                    && oldConnectionWithSameName != null
                    && !oldConnectionWithSameName.uuid.equals(uuid)
                    && connections.contains(oldConnectionWithSameName)) {
                System.out.println("# -------------------------------------------------------------------------- #");
                System.out.println("  WARNING: Two servers have connected with the same name!");
                System.out.println("  1. " + oldConnectionWithSameName.getBungeeCordName() + ": " + oldConnectionWithSameName.getHost() + " (" + oldConnectionWithSameName.uuid + ")");
                System.out.println("  2. " + this.getBungeeCordName() + ": " + this.getHost() + " (" + this.uuid + ")");
                System.out.println("  If this is expected, add -Dallow.multiple.connections to your command line");
                System.out.println("# -------------------------------------------------------------------------- #");
            }
        }

        System.out.println("Connection from " + getAddress() + " (" + name + ")");

        send(new SetSecretMessage(MultiPaperServer.SECRET));
    }

    @Override
    public boolean onMessage(MasterBoundMessage message) {
        lastPing = System.currentTimeMillis();
        return false;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        EntitiesSubscriptionManager.unsubscribeAll(this);
        ChunkSubscriptionManager.unsubscribeAndUnlockAll(this);

        synchronized (connections) {
            connections.remove(this);
        }

        System.out.println(ctx.channel().remoteAddress() + " (" + name + ") closed");
    }

    public String getBungeeCordName() {
        return name;
    }

    public CircularTimer getTimer() {
        return timer;
    }

    public boolean hasPlayer(UUID uuid) {
        synchronized (playerUUIDs) {
            return playerUUIDs.contains(uuid);
        }
    }

    public boolean addPlayer(UUID uuid) {
        synchronized (playerUUIDs) {
            return playerUUIDs.add(uuid);
        }
    }

    public boolean removePlayer(UUID uuid) {
        synchronized (playerUUIDs) {
            return playerUUIDs.remove(uuid);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        if (this.tps == -1) {
            throw new IllegalStateException("Trying to set " + getBungeeCordName() + "'s tps to " + tps + " when it is marked as offline (" + this.tps + " tps)");
        }

        this.tps = tps;
    }

    public SocketAddress getAddress() {
        return channel.remoteAddress();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void handle(CallDataStorageMessage message) {
        CallDataStorageHandler.handle(this, message);
    }

    @Override
    public void handle(ChunkChangedStatusMessage message) {
        ChunkChangedStatusHandler.handle(this, message);
    }

    @Override
    public void handle(DownloadFileMessage message) {
        DownloadFileHandler.handle(this, message);
    }

    @Override
    public void handle(ForceReadChunkMessage message) {
        ForceReadChunkHandler.handle(this, message);
    }

    @Override
    public void handle(LockChunkMessage message) {
        LockChunkHandler.handle(this, message);
    }

    @Override
    public void handle(PlayerConnectMessage message) {
        PlayerConnectHandler.handle(this, message);
    }

    @Override
    public void handle(PlayerDisconnectMessage message) {
        PlayerDisconnectHandler.handle(this, message);
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
    public void handle(RequestChunkOwnershipMessage message) {
        RequestChunkOwnershipHandler.handle(this, message);
    }

    @Override
    public void handle(RequestFilesToSyncMessage message) {
        RequestFilesToSyncHandler.handle(this, message);
    }

    @Override
    public void handle(SetPortMessage message) {
        SetPortHandler.handle(this, message);
    }

    @Override
    public void handle(StartMessage message) {
        StartHandler.handle(this, message);
    }

    @Override
    public void handle(SubscribeChunkMessage message) {
        SubscribeChunkHandler.handle(this, message);
    }

    @Override
    public void handle(SubscribeEntitiesMessage message) {
        SubscribeEntitiesHandler.handle(this, message);
    }

    @Override
    public void handle(SyncChunkOwnerToAllMessage message) {
        SyncChunkOwnerToAllHandler.handle(this, message);
    }

    @Override
    public void handle(SyncChunkSubscribersMessage message) {
        SyncChunkSubscribersHandler.handle(this, message);
    }

    @Override
    public void handle(SyncEntitiesSubscribersMessage message) {
        SyncEntitiesSubscribersHandler.handle(this, message);
    }

    @Override
    public void handle(UnlockChunkMessage message) {
        UnlockChunkHandler.handle(this, message);
    }

    @Override
    public void handle(UnsubscribeChunkMessage message) {
        UnsubscribeChunkHandler.handle(this, message);
    }

    @Override
    public void handle(UnsubscribeEntitiesMessage message) {
        UnsubscribeEntitiesHandler.handle(this, message);
    }

    @Override
    public void handle(UploadFileMessage message) {
        UploadFileHandler.handle(this, message);
    }

    @Override
    public void handle(WillSaveChunkLaterMessage message) {
        WillSaveChunkHandler.handle(this, message);
    }

    @Override
    public void handle(WillSaveEntitiesLaterMessage message) {
        WillSaveEntitiesHandler.handle(this, message);
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

    @Override
    public void handle(RequestEntityIdBlock message) {
        RequestEntityIdBlockHandler.handle(this, message);
    }
}
