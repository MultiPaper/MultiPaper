package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.locks.ChunkLock;

import java.io.DataInputStream;
import java.io.IOException;

public class LockChunkHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String world = in.readUTF();
        int cx = in.readInt();
        int cz = in.readInt();

        String locker = ChunkLock.lock(connection.getBungeeCordName(), world, cx, cz);

        if (locker != null && !ServerConnection.isAlive(locker)) {
            // Ignore dead lockers
            locker = null;
        }

        out.writeUTF("lockedChunk");
        out.writeUTF(locker == null ? "" : locker);
        out.send();
    }
}
