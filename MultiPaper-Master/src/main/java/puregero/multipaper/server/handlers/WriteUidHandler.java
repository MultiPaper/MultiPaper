package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class WriteUidHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String world = in.readUTF();
        byte[] data = new byte[in.readInt()];
        in.readFully(data);

        Worker.runAsync(() -> {
            try {
                File worldDir = new File(world);
                if (!worldDir.exists()) worldDir.mkdirs();
                Files.write(new File(worldDir, "uid.dat").toPath(), data);
                out.writeUTF("uidWritten");
                out.send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
