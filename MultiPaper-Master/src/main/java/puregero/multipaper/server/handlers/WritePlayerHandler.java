package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

public class WritePlayerHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String uuid = in.readUTF();
        byte[] data = new byte[in.readInt()];
        in.readFully(data);

        try {
            FileLocker.writeBytes(new File("world/playerdata", uuid + ".dat"), data);

            out.writeUTF("playerWritten");
            out.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
