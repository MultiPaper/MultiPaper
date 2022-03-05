package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestFilesToSyncHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        List<File> files = new ArrayList<>();

        appendFilesRecursively(new File("synced-server-files"), files);

        out.writeUTF("requestFilesToSync");
        out.writeInt(files.size());
        for (File file : files) {
            out.writeUTF(file.getPath().replace("synced-server-files/", "").replace("synced-server-files\\", ""));
            out.writeLong(file.lastModified());
        }
        out.send();
    }

    private void appendFilesRecursively(File file, List<File> files) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                appendFilesRecursively(child, files);
            }
        } else if (file.isFile()) {
            files.add(file);
        }
    }
}
