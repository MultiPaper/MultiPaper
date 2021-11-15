package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

public class ReadPlayerHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String uuid = in.readUTF();

        readAndSendPlayerData(uuid, out);
    }

    private void readAndSendPlayerData(String uuid, DataOutputSender out) {
        try {
            byte[] b = FileLocker.readBytes(new File("world/playerdata", uuid + ".dat"));
            out.writeUTF("playerData");
            out.writeInt(b.length);
            out.write(b);
            out.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
