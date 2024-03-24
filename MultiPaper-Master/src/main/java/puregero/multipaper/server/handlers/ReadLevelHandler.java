package puregero.multipaper.server.handlers;

import lombok.extern.slf4j.Slf4j;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.ReadLevelMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.DataMessageReply;
import puregero.multipaper.server.ServerConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReadLevelHandler {
    private static final HashMap<String, Long> emptyLevelDatLockTimes = new HashMap<>();
    private static final HashMap<String, String> emptyLevelDatLocker = new HashMap<>();

    public static void handle(ServerConnection connection, ReadLevelMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                try {
                    byte[] b = Files.readAllBytes(new File(message.world, "level.dat").toPath());
                    connection.sendReply(new DataMessageReply(b), message);
                } catch (NoSuchFileException e) {
                    synchronized (emptyLevelDatLockTimes) {
                        if (!connection.getBungeeCordName().equals(emptyLevelDatLocker.get(message.world))) {
                            if (emptyLevelDatLockTimes.getOrDefault(message.world, 0L) > System.currentTimeMillis() - 120 * 1000L) {
                                log.info(connection.getBungeeCordName() + " has requested " + message.world + "'s level.dat, but it is empty. Retying in 10 seconds once " + emptyLevelDatLocker.get(message.world) + " has generated it...");
                                CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> handle(connection, message));
                                return;
                            }

                            log.info(connection.getBungeeCordName() + " has requested " + message.world + "'s level.dat, but it is empty. We will assume this server will generate it.");
                            emptyLevelDatLockTimes.put(message.world, System.currentTimeMillis());
                            emptyLevelDatLocker.put(message.world, connection.getBungeeCordName());
                        }
                    }

                    connection.sendReply(new DataMessageReply(new byte[0]), message);
                }
            } catch (IOException e) {
                log.error("Error reading level.dat", e);
            }
        });
    }
}
