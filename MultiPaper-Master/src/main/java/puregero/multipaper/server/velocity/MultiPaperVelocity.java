package puregero.multipaper.server.velocity;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;
import puregero.multipaper.server.MultiPaperServer;
import puregero.multipaper.server.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@Plugin(id = "multipaper-velocity",
    name = "MultiPaper Velocity",
    version = "1.0.0",
    authors = { "PureGero" }
)
public class MultiPaperVelocity {

    private final ProxyServer server;

    private final Logger logger;

    private final Path dataFolder;

    private int port;

    private boolean balanceNodes;

    @Inject
    public MultiPaperVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Toml config = this.getConfig();

        this.port = Math.toIntExact(config.getLong("port", Long.valueOf(MultiPaperServer.DEFAULT_PORT)));
        this.balanceNodes = config.getBoolean("balance-nodes", true);

        new MultiPaperServer(this.port);
    }

    @Subscribe
    public void onServerConnect(ServerPreConnectEvent event) {
        RegisteredServer targetServer = event.getResult().getServer().get();

        if (this.balanceNodes && isMultiPaperServer(targetServer.getServerInfo().getName())) {
            Collection<RegisteredServer> servers = this.server.getAllServers();

            RegisteredServer bestServer = null;
            long lowestTickTime = Long.MAX_VALUE;

            for (RegisteredServer server : servers) {
                String serverName = server.getServerInfo().getName();
                ServerConnection connection = ServerConnection.getConnection(serverName);

                if (connection != null && ServerConnection.isAlive(serverName)
                        && connection.getTimer().averageInMillis() < lowestTickTime) {
                    lowestTickTime = connection.getTimer().averageInMillis();
                    bestServer = server;
                }
            }

            if (bestServer != null) {
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(bestServer));
            }
        }
    }

    private boolean isMultiPaperServer(String name) {
        return ServerConnection.getConnection(name) != null;
    }

    private Toml getConfig() {
        File dataFolder = this.dataFolder.toFile();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File file = new File(dataFolder, "config.toml");

        if (!file.exists()) {
            try (InputStream in = getClass().getClassLoader()
                    .getResourceAsStream("config.toml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new Toml().read(file);
    }
}
