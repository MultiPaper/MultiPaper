package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class WriteDataHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String path = in.readUTF();
        byte[] data = new byte[in.readInt()];
        in.readFully(data);

        Worker.runAsync(() -> {
            try {
                FileLocker.writeBytes(new File(path), data);
                out.writeUTF("dataWritten");
                out.send();

                DataOutputStream broadcast = connection.broadcastOthers();
                broadcast.writeInt(-1);
                broadcast.writeUTF("clearData");
                broadcast.writeUTF(path);
                broadcast.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
