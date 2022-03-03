package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PlayerConnectHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        UUID uuid = UUID.fromString(in.readUTF());

        List<ServerConnection> connections = ServerConnection.getConnections();

        synchronized (connections) {
            for (ServerConnection otherConnection : connections) {
                if (otherConnection != connection && otherConnection.hasPlayer(uuid)) {
                    out.writeUTF("playerJoin");
                    out.writeBoolean(false);
                    out.send();
                }
            }

            connection.addPlayer(uuid);
        }

        out.writeUTF("playerConnect");
        out.writeBoolean(true);
        out.send();
    }
}
