package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;
import puregero.multipaper.server.locks.AdvancementsLock;
import puregero.multipaper.server.locks.PlayerLock;

import java.io.DataInputStream;
import java.io.IOException;

public class ReleasePlayerHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String uuid = in.readUTF();

        Worker.runAsync(() -> PlayerLock.release(connection.getBungeeCordName(), uuid));
    }
}
