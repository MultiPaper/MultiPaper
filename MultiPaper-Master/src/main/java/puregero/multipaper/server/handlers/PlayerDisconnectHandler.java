package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerDisconnectHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        UUID uuid = UUID.fromString(in.readUTF());

        connection.removePlayer(uuid);
    }
}
