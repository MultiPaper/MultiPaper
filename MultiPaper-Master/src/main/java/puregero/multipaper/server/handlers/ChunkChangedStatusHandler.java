package puregero.multipaper.server.handlers;

import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ChunkChangedStatusHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out2) throws IOException {
        String world = in.readUTF();
        int cx = in.readInt();
        int cz = in.readInt();
        String status = in.readUTF();

        for (ServerConnection subscriber : ChunkSubscriptionManager.getSubscribers(world, cx, cz)) {
            CompletableFuture.runAsync(() -> {
                try {
                    DataOutputSender out = subscriber.buffer();
                    out.writeUTF("chunkChangedStatus");
                    out.writeUTF(world);
                    out.writeInt(cx);
                    out.writeInt(cz);
                    out.writeUTF(status);
                    out.writeUTF(connection.getBungeeCordName());
                    out.send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
