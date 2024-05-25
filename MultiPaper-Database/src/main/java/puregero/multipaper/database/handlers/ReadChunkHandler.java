package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.ReadChunkMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.ChunkLoadedOnAnotherServerMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.DataMessageReply;
import puregero.multipaper.database.*;
import puregero.multipaper.database.util.RegionFileCache;

import java.io.File;

public class ReadChunkHandler {
    public static void handle(ServerConnection connection, ReadChunkMessage message) {
        if (checkIfLoadedOnAnotherServer(connection, message.world, message.path, message.cx, message.cz, message)) {
            return;
        }

        Runnable callback = () -> {
            RegionFileCache.getChunkDeflatedDataAsync(getWorldDir(message.world, message.path), message.cx, message.cz).thenAccept(b -> {
                if (b == null) {
                    b = new byte[0];
                }
                connection.sendReply(new DataMessageReply(b), message);
            });
        };

        if (message.path.equals("region")) {
            ChunkLockManager.waitForLock(message.world, message.cx, message.cz, callback);
        } else if (message.path.equals("entities")) {
            EntitiesLockManager.waitForLock(message.world, message.cx, message.cz, callback);
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
