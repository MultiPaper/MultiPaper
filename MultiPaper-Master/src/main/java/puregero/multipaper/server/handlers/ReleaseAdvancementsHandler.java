package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;
import puregero.multipaper.server.handlers.Handler;
import puregero.multipaper.server.locks.AdvancementsLock;
import puregero.multipaper.server.locks.PlayerLock;

import java.io.DataInputStream;
import java.io.IOException;

public class ReleaseAdvancementsHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String uuid = in.readUTF();

        Worker.runAsync(() -> AdvancementsLock.release(connection.getBungeeCordName(), uuid));
    }
}
