package puregero.multipaper.server.handlers;

import puregero.multipaper.server.*;
import puregero.multipaper.server.util.RegionFileCache;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

public class ReadChunkHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String world = in.readUTF();
        String path = in.readUTF();
        int cx = in.readInt();
        int cz = in.readInt();

        ChunkLockManager.waitForLock(world, cx, cz, () -> {
            try {
                if (path.equals("region")) {
                    ServerConnection alreadyLoadedChunk = ChunkSubscriptionManager.getOwnerOrSubscriber(world, cx, cz);
                    ChunkSubscriptionManager.subscribe(connection, world, cx, cz);
                    if (alreadyLoadedChunk != null && alreadyLoadedChunk != connection) {
                        out.writeUTF("chunkData");
                        out.writeUTF(alreadyLoadedChunk.getBungeeCordName());
                        out.writeInt(0);
                        out.send();
                        return;
                    }
                }

                if (path.equals("entities")) {
                    ServerConnection alreadyLoadedEntities = EntitiesSubscriptionManager.getSubscriber(world, cx, cz);
                    EntitiesSubscriptionManager.subscribe(connection, world, cx, cz);
                    if (alreadyLoadedEntities != null && alreadyLoadedEntities != connection) {
                        out.writeUTF("chunkData");
                        out.writeUTF(alreadyLoadedEntities.getBungeeCordName());
                        out.writeInt(0);
                        out.send();
                        return;
                    }
                }

                byte[] b = RegionFileCache.getChunkDeflatedData(getWorldDir(world, path), cx, cz);
                if (b == null) {
                    b = new byte[0];
                }
                out.writeUTF("chunkData");
                out.writeUTF("");
                out.writeInt(b.length);
                out.write(b);
                out.send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
