package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.locks.ChunkLock;

import java.io.DataInputStream;
import java.io.IOException;

public class LockChunkHandler implements Handler {
    private static final int CHUNK_SEARCH_RADIUS = 3;
    
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String locker;
        
        String world = in.readUTF();
        int cx = in.readInt();
        int cz = in.readInt();

        // Check if any chunks around this chunk are locked
        for (int x = -CHUNK_SEARCH_RADIUS; x <= CHUNK_SEARCH_RADIUS; x++) {
            for (int z = -CHUNK_SEARCH_RADIUS; z <= CHUNK_SEARCH_RADIUS; z++) {
                locker = ChunkLock.getLockHolder(world, cx + x, cz + z);
                if (locker != null && !locker.equals(connection.getBungeeCordName()) && ServerConnection.isAlive(locker)) {
                    out.writeUTF("lockedChunk");
                    out.writeUTF(locker);
                    out.send();
                    return;
                }
            }
        }
        
        if (world.contains("_the_end")) {
            // Do nothing for the end
        } else if (world.contains("_nether")) {
            // Check if overworld equiv chunk is locked
            String worldName = world.split("_")[0];
            locker = ChunkLock.getLockHolder(worldName, cx * 8, cz * 8);
            if (locker != null && !locker.equals(connection.getBungeeCordName()) && ServerConnection.isAlive(locker)) {
                out.writeUTF("lockedChunk");
                out.writeUTF(locker);
                out.send();
                return;
            }
        } else {
            // Check if nether equiv chunk is locked
            locker = ChunkLock.getLockHolder(world + "_nether", cx / 8, cz / 8);
            if (locker != null && !locker.equals(connection.getBungeeCordName()) && ServerConnection.isAlive(locker)) {
                out.writeUTF("lockedChunk");
                out.writeUTF(locker);
                out.send();
                return;
            }
        }
        
        locker = ChunkLock.lock(connection.getBungeeCordName(), world, cx, cz);

        if (locker != null && !ServerConnection.isAlive(locker)) {
            // Ignore dead lockers
            locker = null;
        }

        out.writeUTF("lockedChunk");
        out.writeUTF(locker == null ? "" : locker);
        out.send();
    }
}
