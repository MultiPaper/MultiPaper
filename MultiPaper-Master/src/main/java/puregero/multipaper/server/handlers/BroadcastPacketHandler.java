package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;
import puregero.multipaper.server.locks.AdvancementsLock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class BroadcastPacketHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String className = in.readUTF();
        int length = in.readInt();
        byte[] b = new byte[length];
        in.readFully(b);

        Worker.runAsync(() -> {
            try {
                DataOutputStream broadcast = connection.broadcastOthers();
                broadcast.writeInt(-1);
                broadcast.writeUTF("broadcastPacket");
                broadcast.writeUTF(className);
                broadcast.writeInt(length);
                broadcast.write(b);
                broadcast.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
