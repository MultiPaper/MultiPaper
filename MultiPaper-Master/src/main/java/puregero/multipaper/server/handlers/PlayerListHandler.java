package puregero.multipaper.server.handlers;

import puregero.multipaper.server.*;
import puregero.multipaper.server.locks.PlayerLock;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

public class PlayerListHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        connection.getPlayers().clear();

        connection.setTps(in.readDouble());

        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            connection.getPlayers().add(Player.read(in));
        }
    }
}
