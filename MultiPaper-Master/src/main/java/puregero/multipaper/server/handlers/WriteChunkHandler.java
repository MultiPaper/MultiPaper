package puregero.multipaper.server.handlers;

import puregero.multipaper.server.ChunkLockManager;
import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.util.RegionFileCache;

import java.io.DataInputStream;
import java.io.IOException;

public class WriteChunkHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String world = in.readUTF();
        String path = in.readUTF();
        int cx = in.readInt();
        int cz = in.readInt();
        byte[] data = new byte[in.readInt()];
        in.readFully(data);

        if (path.equals("region")) {
            ChunkLockManager.writtenChunk(world, cx, cz);
        }

        try {
            RegionFileCache.putChunkDeflatedData(ReadChunkHandler.getWorldDir(world, path), cx, cz, data);
            out.writeUTF("chunkWritten");
            out.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
