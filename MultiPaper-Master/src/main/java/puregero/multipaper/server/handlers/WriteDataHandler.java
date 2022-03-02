package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class WriteDataHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String path = in.readUTF();
        byte[] data = new byte[in.readInt()];
        in.readFully(data);

        try {
            FileLocker.writeBytes(new File(path), data);
            out.writeUTF("dataWritten");
            out.send();

            if (path.contains("scoreboard")) {
                // Scoreboards are synced with other methods
                return;
            }

            DataOutputStream broadcast = connection.broadcastOthers();
            broadcast.writeInt(-1);
            broadcast.writeUTF("clearData");
            broadcast.writeUTF(path);
            broadcast.writeInt(data.length);
            broadcast.write(data);
            broadcast.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
