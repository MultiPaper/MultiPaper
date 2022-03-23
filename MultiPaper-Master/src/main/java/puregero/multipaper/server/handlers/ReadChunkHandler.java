package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.ReadChunkMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ChunkLoadedOnAnotherServerMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.DataMessageReply;
import puregero.multipaper.server.ChunkLockManager;
import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.EntitiesSubscriptionManager;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.util.RegionFileCache;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class ReadChunkHandler {
    public static void handle(ServerConnection connection, ReadChunkMessage message) {
        if (checkIfLoadedOnAnotherServer(connection, message.world, message.path, message.cx, message.cz, message)) {
            return;
        }

        Runnable callback = () -> {
            CompletableFuture.runAsync(() -> {
                byte[] b = RegionFileCache.getChunkDeflatedData(getWorldDir(message.world, message.path), message.cx, message.cz);
                if (b == null) {
                    b = new byte[0];
                }
                connection.sendReply(new DataMessageReply(b), message);
            });
        };

        if (message.path.equals("region")) {
            ChunkLockManager.waitForLock(message.world, message.cx, message.cz, callback);
        } else {
            callback.run();
        }
    }

    private static boolean checkIfLoadedOnAnotherServer(ServerConnection connection, String world, String path, int cx, int cz, ReadChunkMessage message) {
        if (path.equals("region")) {
            ServerConnection alreadyLoadedChunk = ChunkSubscriptionManager.getOwnerOrSubscriber(world, cx, cz);
            ChunkSubscriptionManager.subscribe(connection, world, cx, cz);
            if (alreadyLoadedChunk != null && alreadyLoadedChunk != connection) {
                connection.sendReply(new ChunkLoadedOnAnotherServerMessage(alreadyLoadedChunk.getBungeeCordName()), message);
                return true;
            }
        }

        if (path.equals("entities")) {
            ServerConnection alreadyLoadedEntities = EntitiesSubscriptionManager.getSubscriber(world, cx, cz);
            EntitiesSubscriptionManager.subscribe(connection, world, cx, cz);
            if (alreadyLoadedEntities != null && alreadyLoadedEntities != connection) {
                connection.sendReply(new ChunkLoadedOnAnotherServerMessage(alreadyLoadedEntities.getBungeeCordName()), message);
                return true;
            }
        }

        return false;
    }

    static File getWorldDir(String world, String path) {
        File file = new File(world);

        if (world.endsWith("_nether")) {
            file = new File(file, "DIM-1");
        }

        if (world.endsWith("_the_end")) {
            file = new File(file, "DIM1");
        }

        return new File(file, path);
    }
}
