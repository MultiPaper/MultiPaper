package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReadJsonHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String file = in.readUTF();

        Worker.runAsync(() -> {
            try {
                byte[] b = Files.readAllBytes(new File(file).toPath());
                out.writeUTF("jsonData");
                out.writeInt(b.length);
                out.write(b);
                out.send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
