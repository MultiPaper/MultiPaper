package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

public class ReadDataHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String path = in.readUTF();

        try {
            byte[] b = FileLocker.readBytes(new File(path));
            out.writeUTF("dataData");
            out.writeInt(b.length);
            out.write(b);
            out.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
