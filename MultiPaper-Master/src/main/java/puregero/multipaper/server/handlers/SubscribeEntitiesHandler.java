package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.EntitiesSubscriptionManager;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.IOException;

public class SubscribeEntitiesHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String world = in.readUTF();
        int cx = in.readInt();
        int cz = in.readInt();

        EntitiesSubscriptionManager.subscribe(connection, world, cx, cz);
    }
}
