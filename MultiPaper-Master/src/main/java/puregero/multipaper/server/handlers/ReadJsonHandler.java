package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class ReadJsonHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String file = in.readUTF();

        try {
            synchronized (WriteJsonHandler.writesInProgress) {
                if (WriteJsonHandler.writesInProgress.containsKey(file)) {
                    byte[] b = WriteJsonHandler.writesInProgress.get(file);
                    out.writeUTF("jsonData");
                    out.writeInt(b.length);
                    out.write(b);
                    out.send();
                    return;
                }
            }

            try{
                byte[] b = Files.readAllBytes(new File(file).toPath());
                out.writeUTF("jsonData");
                out.writeInt(b.length);
                out.write(b);
                out.send();
            } catch (NoSuchFileException e) {
                out.writeUTF("jsonData");
                out.writeInt(0);
                out.send();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
