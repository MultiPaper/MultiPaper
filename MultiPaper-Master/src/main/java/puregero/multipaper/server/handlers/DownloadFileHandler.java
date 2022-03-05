package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class DownloadFileHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String path = in.readUTF();

        CompletableFuture.runAsync(() -> {
            try {
                File file = new File("synced-server-files", path);
                byte[] b = FileLocker.readBytes(file);
                out.writeUTF("downloadFile");
                out.writeLong(file.lastModified());
                out.writeLong(b.length);
                out.write(b);
                out.send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
