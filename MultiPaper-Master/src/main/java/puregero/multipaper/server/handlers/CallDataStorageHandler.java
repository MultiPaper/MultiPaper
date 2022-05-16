package puregero.multipaper.server.handlers;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.CallDataStorageMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.NullableStringMessageReply;
import puregero.multipaper.server.ServerConnection;

import java.io.*;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CallDataStorageHandler {
    private static Map<String, Object> yaml;
    private static CompletableFuture<Void> saveFuture;

    public static void handle(ServerConnection connection, CallDataStorageMessage message) {
        CompletableFuture.runAsync(() -> {
            String result = handleMessage(message);
            connection.sendReply(new NullableStringMessageReply(result), message);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    private static synchronized String handleMessage(CallDataStorageMessage message) {
        loadYaml();

        String key = message.key;
        String value = message.value;

        switch (message.action) {
            case GET -> {
                return (String) yaml.get(key);
            }
            case SET -> {
                if (value == null) {
                    yaml.remove(key);
                } else {
                    yaml.put(key, value);
                }
                scheduleSave();
                return value;
            }
            case ADD -> {
                String result = add((String) yaml.get(key), value);
                yaml.put(key, result);
                scheduleSave();
                return result;
            }
            default -> {
                throw new IllegalArgumentException("Unknown action " + message.action);
            }
        }
    }

    private static String add(String A, String B) {
        if (A == null) {
            return B;
        }

        try {
            long a = Long.parseLong(A);
            long b = Long.parseLong(B);
            return Long.toString(a + b);
        } catch (NumberFormatException ignored) {}

        try {
            double a = Double.parseDouble(A);
            double b = Double.parseDouble(B);
            return Double.toString(a + b);
        } catch (NumberFormatException ignored) {}

        return B;
    }

    private static synchronized void scheduleSave() {
        if (saveFuture == null || saveFuture.isDone()) {
            saveFuture = CompletableFuture.runAsync(CallDataStorageHandler::saveYaml, CompletableFuture.delayedExecutor(15, TimeUnit.SECONDS));
        }
    }

    private static synchronized void saveYaml() {
        try {
            Path file = Path.of("datastorage.yml");
            Path tempFile = Files.createTempFile("datastorage.", ".yml");

            try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                DumperOptions options = new DumperOptions();
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                options.setPrettyFlow(true);
                new Yaml(options).dump(yaml, writer);
            }

            try {
                Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void loadYaml() {
        if (yaml == null) {
            File file = new File("datastorage.yml");

            // Who called it .yaml... (backwards compatibility)
            if (!file.isFile()) {
                file = new File("datastorage.yaml");
            }

            if (file.isFile()) {
                try (FileInputStream in = new FileInputStream(file)) {
                    yaml = new Yaml().load(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (yaml == null) {
                yaml = new HashMap<>();
            }

            registerShutdownHook();
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread("datastorage-saver") {
            @Override
            public void run() {
                if (saveFuture != null && !saveFuture.isDone()) {
                    System.out.println("Saving unsaved datastorage.yaml...");
                    saveYaml();
                }
            }
        });
    }
}
