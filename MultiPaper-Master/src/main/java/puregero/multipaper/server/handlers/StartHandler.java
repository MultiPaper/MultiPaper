package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;
import puregero.multipaper.server.locks.ChunkLock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StartHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        Worker.runAsync(() -> {
            try {
                System.out.println(connection.getBungeeCordName() + " has started, broadcasting start to other servers...");
                DataOutputStream broadcast = connection.broadcastOthers();
                broadcast.writeInt(-1);
                broadcast.writeUTF("start");
                broadcast.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            ChunkLock.releaseAllLocks(connection.getBungeeCordName());
        });
    }
}
