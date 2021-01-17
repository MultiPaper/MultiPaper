package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class WriteLevelHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String world = in.readUTF();
        byte[] data = new byte[in.readInt()];
        in.readFully(data);

        Worker.runAsync(() -> {
            try {
                Files.write(new File(world, "level.dat").toPath(), data);
                out.writeUTF("levelWritten");
                out.send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
