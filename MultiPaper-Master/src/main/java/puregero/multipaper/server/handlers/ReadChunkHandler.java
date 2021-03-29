package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;
import puregero.multipaper.server.locks.ChunkLock;
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

        Worker.runAsync(() -> {
            try {
                long t = System.currentTimeMillis();
                while (ChunkLock.isBeingWritten(world, cx, cz) != null && !connection.getBungeeCordName().equals(ChunkLock.isBeingWritten(world, cx, cz))) {
                    if (t < System.currentTimeMillis() - 20 * 1000) {
                        new IllegalStateException("Timed out while waiting for chunk to be written (" + world + "," + cx + "," + cz + ")").printStackTrace();
                        break;
                    }
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }

                byte[] b = RegionFileCache.getChunkDeflatedData(getWorldDir(world, path), cx, cz);
                if (b == null) {
                    b = new byte[0];
                }
                out.writeUTF("chunkData");
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
