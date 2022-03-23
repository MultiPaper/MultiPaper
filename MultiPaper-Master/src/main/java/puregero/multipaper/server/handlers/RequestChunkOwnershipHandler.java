package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.ChunkKey;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.RequestChunkOwnershipMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class RequestChunkOwnershipHandler {
    public static void handle(ServerConnection connection, RequestChunkOwnershipMessage message) {
        boolean hasAtLeastOneChunkLocked = false;
        for (ChunkKey key : message.chunks) {
            if (ChunkSubscriptionManager.getOwner(key.world, key.x, key.z) == connection) {
                hasAtLeastOneChunkLocked = true;
            }
        }

        System.out.println(connection.getBungeeCordName() + " is requesting " + Arrays.toString(message.chunks) + " " + hasAtLeastOneChunkLocked);

        if (hasAtLeastOneChunkLocked) {
            for (ChunkKey key : message.chunks) {
                ChunkSubscriptionManager.lock(connection, key.world, key.x, key.z, true);
            }

            CompletableFuture.runAsync(() -> {
                // Use runAsync to run this after it's sent all the other lock data
                connection.sendReply(new BooleanMessageReply(true), message);
            });
        } else {
            connection.sendReply(new BooleanMessageReply(false), message);
        }
    }
}
