package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.IOException;

public interface Handler {
    void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException;
}
