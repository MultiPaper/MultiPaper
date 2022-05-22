package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.messages.masterbound.WriteChunkMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.server.ChunkLockManager;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.util.RegionFileCache;

import java.util.concurrent.CompletableFuture;

public class WriteChunkHandler {
    public static void handle(ServerConnection connection, WriteChunkMessage message) {
        CompletableFuture.runAsync(() -> {
            RegionFileCache.putChunkDeflatedData(ReadChunkHandler.getWorldDir(message.world, message.path), message.cx, message.cz, message.data);

            if (message.path.equals("region")) {
                ChunkLockManager.writtenChunk(message.world, message.cx, message.cz);
            }

            connection.sendReply(new BooleanMessageReply(true), message);
        });
    }
}
