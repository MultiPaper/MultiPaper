package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class UploadFileHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        boolean immediatelySyncToOtherServers = in.readBoolean();
        String path = in.readUTF();
        long lastModified = in.readLong();
        long length = in.readLong();

        if (length > Integer.MAX_VALUE) {
            throw new IOException("File uploads are currently limited to 2GB. " + path + " exceeds this limit with " + length + " bytes");
        }

        byte[] data = new byte[(int) length];
        in.readFully(data);

        try {
            File file = new File("synced-server-files", path);
            FileLocker.writeBytes(file, data);
            file.setLastModified(lastModified);

            out.writeUTF("uploadFile");
            out.send();

            if (immediatelySyncToOtherServers) {
                DataOutputStream broadcast = connection.broadcastOthers();
                broadcast.writeInt(-1);
                broadcast.writeUTF("syncFile");
                broadcast.writeUTF(path);
                broadcast.writeLong(lastModified);
                broadcast.writeLong(data.length);
                broadcast.write(data);
                broadcast.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
