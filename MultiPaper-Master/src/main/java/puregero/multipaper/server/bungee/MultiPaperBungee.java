package puregero.multipaper.server.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import puregero.multipaper.server.MultiPaperServer;
import puregero.multipaper.server.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class MultiPaperBungee extends Plugin implements Listener {

    private final HashSet<ProxiedPlayer> usingServerCommand = new HashSet<>();
    private boolean balanceNodes = true;

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);

        File config = new File(getDataFolder(), "config.yml");

        if (!config.isFile()) {
            config.getParentFile().mkdirs();
            try {
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);

            if (!configuration.contains("port")) {
                configuration.set("port", MultiPaperServer.DEFAULT_PORT);
            }

            if (!configuration.contains("balanceNodes")) {
                configuration.set("balanceNodes", true);
            }

            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(getDataFolder(), "config.yml"));

            balanceNodes = configuration.getBoolean("balanceNodes", true);

            new MultiPaperServer(configuration.getInt("port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.getMessage().startsWith("/server ") && event.getSender() instanceof ProxiedPlayer proxiedPlayer && proxiedPlayer.hasPermission("multipaper.directserverconnect")) {
            usingServerCommand.add((ProxiedPlayer) event.getSender());
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (usingServerCommand.remove(event.getPlayer())) {
            // Allow players to do /server <server> to join the server that they want
            // (Mostly for debug purposes)
            return;
        }

        if (balanceNodes && isMultiPaperServer(event.getTarget().getName())) {
            // They are connecting to a multipaper server

            List<ServerInfo> servers = new ArrayList<>(getProxy().getServers().values());
            Collections.shuffle(servers);

            // Send them to the multipaper server with the lowest tick time
            ServerInfo bestServer = null;
            long lowestTickTime = Long.MAX_VALUE;

            for (ServerInfo info : servers) {
                ServerConnection connection = ServerConnection.getConnection(info.getName());
                if (connection != null && ServerConnection.isAlive(info.getName()) && connection.getTimer().averageInMillis() < lowestTickTime) {
                    lowestTickTime = connection.getTimer().averageInMillis();
                    bestServer = info;
                }
            }

            if (bestServer != null) {
                event.setTarget(bestServer);
            }
        }
    }

    private boolean isMultiPaperServer(String name) {
        return ServerConnection.getConnection(name) != null;
    }

}
