package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.locks.ChunkLock;
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

        try {
            RegionFileCache.putChunkDeflatedData(ReadChunkHandler.getWorldDir(world, path), cx, cz, data);
            out.writeUTF("chunkWritten");
            out.send();

            if (path.equals("region")) {
                ChunkLock.chunkWritten(connection, world, cx, cz);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
