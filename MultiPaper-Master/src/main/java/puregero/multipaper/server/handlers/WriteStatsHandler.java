package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;
import puregero.multipaper.server.handlers.Handler;
import puregero.multipaper.server.locks.StatsLock;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

public class WriteStatsHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String uuid = in.readUTF();
        byte[] data = new byte[in.readInt()];
        in.readFully(data);

        Worker.runAsync(() -> {
            try {
                String lockHolder = StatsLock.getLockHolder(uuid);

                if (lockHolder == null || lockHolder.equals(connection.getBungeeCordName())) {
                    FileLocker.writeBytes(new File("world/stats", uuid + ".json"), data);
                }

                out.writeUTF("statsWritten");
                out.send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
