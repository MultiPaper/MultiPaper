package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.ChunkSubscriptionManager;

import java.io.DataInputStream;
import java.io.IOException;

public class UnsubscribeChunkHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String world = in.readUTF();
        int cx = in.readInt();
        int cz = in.readInt();

        ChunkSubscriptionManager.unsubscribe(connection, world, cx, cz);

        try {
            out.writeUTF("unsubscribeChunk");
            out.writeUTF(world);
            out.writeInt(cx);
            out.writeInt(cz);
            out.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
