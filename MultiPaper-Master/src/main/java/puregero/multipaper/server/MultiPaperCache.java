package puregero.multipaper.server;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.YamlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MultiPaperCache {
    private static HazelcastInstance hazelcast = null;
    private static boolean isShutdown = false;
    private static final File file = new File("hazelcast.yml");

    public static void initialize() {
        if (hazelcast != null)
            return;

        try {
            if (!file.exists()) {
                try (InputStream is = MultiPaperCache.class.getClassLoader().getResourceAsStream(file.getName())) {
                    file.createNewFile();
                    is.transferTo(new FileOutputStream(file, false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ClientConfig cfg = new YamlClientConfigBuilder(file.getName()).build();
            hazelcast = HazelcastClient.newHazelcastClient(cfg);
            isShutdown = false;

            // Configure logging level of JDK's logger
            if (hazelcast.getConfig().getProperty("hazelcast.logging.type").equals("jdk")) {
                Logger logger = LogManager.getLogManager().getLogger("");
                logger.setLevel(Level.FINE);
                for (Handler h : logger.getHandlers())
                    h.setLevel(Level.FINE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Started Hazelcast instance...");
    }


    public static void shutdown() {
        isShutdown = true;
        hazelcast.shutdown();
    }

    public static HazelcastInstance getHazelcast() {
        if (!isShutdown) {
            initialize();
            return hazelcast;
        }
        return null;
    }
}