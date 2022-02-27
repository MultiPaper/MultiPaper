package puregero.multipaper.server.handlers;

import puregero.multipaper.server.ChunkSubscriptionManager;
import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SyncChunkOwnerToAllHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String world = in.readUTF();
        int cx = in.readInt();
        int cz = in.readInt();

        ServerConnection owner = ChunkSubscriptionManager.getOwner(world, cx, cz);

        DataOutputStream broadcast = ServerConnection.broadcastAll();
        broadcast.writeInt(-1);
        broadcast.writeUTF("chunkOwner");
        broadcast.writeUTF(world);
        broadcast.writeInt(cx);
        broadcast.writeInt(cz);
        broadcast.writeUTF(owner == null ? "" : owner.getBungeeCordName());
        broadcast.close();
    }
}
