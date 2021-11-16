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

        ServerConnection owner = ChunkLock.lock(connection, world, cx, cz);

        out.writeUTF("chunkOwner");
        out.writeUTF(world);
        out.writeInt(cx);
        out.writeInt(cz);
        out.writeUTF(owner.getBungeeCordName());
        out.send();
    }
}
