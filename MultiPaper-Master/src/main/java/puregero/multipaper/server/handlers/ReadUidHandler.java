package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReadUidHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String world = in.readUTF();

        Worker.runAsync(() -> {
            try {
                byte[] b = Files.readAllBytes(new File(world, "uid.dat").toPath());
                out.writeUTF("uidData");
                out.writeInt(b.length);
                out.write(b);
                out.send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
