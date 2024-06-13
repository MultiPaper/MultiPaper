package io.multipaper.database;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServerInfo {

    private final String name;
    private final UUID uuid;

    private long lastMessageTime = System.currentTimeMillis();
    private final CircularTimer timer = new CircularTimer();
    private final Set<UUID> playerUUIDs = ConcurrentHashMap.newKeySet();
    private double tps;
    private int port = -1;
    private String host;

    private static final Map<UUID, ServerInfo> servers = new ConcurrentHashMap<>();
    private static final Map<String, ServerInfo> serversByName = new ConcurrentHashMap<>();

    public static ServerInfo getOrCreate(UUID uuid, String name) {
        return servers.computeIfAbsent(uuid, k -> {
            ServerInfo serverInfo = new ServerInfo(uuid, name);
            serversByName.put(name, serverInfo);
            return serverInfo;
        });
    }

    public static ServerInfo getByName(String name) {
        return serversByName.get(name);
    }

    public static Collection<ServerInfo> getServers() {
        return servers.values();
    }

    public ServerInfo(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public boolean isAlive() {
        return lastMessageTime > System.currentTimeMillis() - 15000 && tps > 0;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public CircularTimer getTimer() {
        return timer;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String hostAddress) {
        this.host = hostAddress;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
