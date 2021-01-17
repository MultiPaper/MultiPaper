package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.MultiPaperServer;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;
import puregero.multipaper.server.locks.ChunkLock;
import puregero.multipaper.server.util.RegionFileCache;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class LoadedChunkListHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        int count = in.readInt();

        HashSet<String> chunks = new HashSet<>();

        for (int i = 0; i < count; i++) {
            String world = in.readUTF();
            int cx = in.readInt();
            int cz = in.readInt();

            String chunk = world + "," + cx + "," + cz;

            for (ServerConnection connection2 : ServerConnection.getConnections()) {
                if (connection != connection2 && connection2.loadedChunks.contains(chunk)) {
                    System.out.println("Both " + connection.getBungeeCordName() + " and " + connection2.getBungeeCordName() + " have chunk " + chunk + " loaded!");
                }
            }
        }

        connection.loadedChunks = chunks;
    }
}
