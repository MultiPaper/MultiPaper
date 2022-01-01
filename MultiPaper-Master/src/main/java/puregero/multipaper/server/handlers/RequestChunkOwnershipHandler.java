package puregero.multipaper.server.handlers;

import puregero.multipaper.server.ChunkKey;
import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RequestChunkOwnershipHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        int count = in.readInt();
        String world = in.readUTF();

        List<ChunkKey> chunks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int cx = in.readInt();
            int cz = in.readInt();
            chunks.add(new ChunkKey(world, cx, cz));
        }

        boolean hasAtLeastOneChunkLocked = false;
        for (ChunkKey key : chunks) {
            if (ChunkSubscriptionManager.getOwner(key.name, key.x, key.z) == connection) {
                hasAtLeastOneChunkLocked = true;
            }
        }

        System.out.println(connection.getBungeeCordName() + " is requesting " + chunks + " " + hasAtLeastOneChunkLocked);

        if (hasAtLeastOneChunkLocked) {
            for (ChunkKey key : chunks) {
                ChunkSubscriptionManager.lock(connection, key.name, key.x, key.z, true);
            }

            CompletableFuture.runAsync(() -> {
                // Use runAsync to run this after it's sent all the other lock data
                try {
                    out.writeUTF("requestChunkOwnership");
                    out.writeBoolean(true);
                    out.send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            out.writeUTF("requestChunkOwnership");
            out.writeBoolean(false);
            out.send();
        }
    }
}
