package puregero.multipaper.server.handlers;

import lombok.extern.slf4j.Slf4j;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.RequestEntityIdBlock;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.IntegerPairMessageReply;
import puregero.multipaper.server.ServerConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RequestEntityIdBlockHandler {

    private static final int BLOCK_SIZE = Integer.getInteger("entityid.block.size", 4096);
    private static AtomicInteger lastBlock = null;
    private static volatile boolean queueLastBlockSave = true;
    private static CompletableFuture<Void> lastBlockWriteInProgress = CompletableFuture.completedFuture(null);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(RequestEntityIdBlockHandler::saveLastBlock));
    }

    public static void handle(ServerConnection connection, RequestEntityIdBlock message) {
        if (lastBlock == null) {
            loadLastBlock();
        }

        int blockStart = lastBlock.getAndAdd(BLOCK_SIZE);
        connection.sendReply(new IntegerPairMessageReply(blockStart, blockStart + BLOCK_SIZE), message);

        queueLastBlockSave = true;
        if (lastBlockWriteInProgress.isDone()) {
            lastBlockWriteInProgress = lastBlockWriteInProgress.thenRunAsync(RequestEntityIdBlockHandler::saveLastBlock);
        }
    }

    private static synchronized void loadLastBlock() {
        if (lastBlock != null) {
            // Already been loaded by another thread
            return;
        }

        try {
            String str = Files.readString(Path.of("lastblock.txt"));
            lastBlock = new AtomicInteger(Integer.parseInt(str));
            return;
        } catch (NoSuchFileException ignored) {
        } catch (Exception e) {
            log.error("Failed to load last block", e);
        }

        lastBlock = new AtomicInteger(0);
    }

    private static synchronized void saveLastBlock() {
        if (queueLastBlockSave) {
            queueLastBlockSave = false;
            try {
                Files.writeString(Path.of("lastblock.txt"), Integer.toString(lastBlock.get()));
            } catch (IOException e) {
                log.error("Failed to save last block", e);
            }
        }

        if (queueLastBlockSave) {
            // Has been requeued during the I/O operation
            saveLastBlock();
        }
    }
}
