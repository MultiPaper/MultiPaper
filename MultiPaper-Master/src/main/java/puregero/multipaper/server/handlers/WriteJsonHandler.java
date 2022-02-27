package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class WriteJsonHandler implements Handler {
    public static final HashMap<String, byte[]> writesInProgress = new HashMap<>();
    private static final Object writingLock = new Object();

    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String file = in.readUTF();
        byte[] data = new byte[in.readInt()];
        in.readFully(data);

        try {
            synchronized (writingLock) {
                synchronized (writesInProgress) {
                    writesInProgress.put(file, data);
                }

                Files.write(new File(file).toPath(), data);

                synchronized (writesInProgress) {
                    writesInProgress.remove(file);
                }
            }

            out.writeUTF("jsonWritten");
            out.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
