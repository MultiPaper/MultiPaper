package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class WriteJsonHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String file = in.readUTF();
        byte[] data = new byte[in.readInt()];
        in.readFully(data);

        Worker.runAsync(() -> {
            try {
                Files.write(new File(file).toPath(), data);
                out.writeUTF("jsonWritten");
                out.send();

                System.out.println(connection.getBungeeCordName() + " has updated " + file + ", broadcasting reload request...");
                DataOutputStream broadcast = connection.broadcastOthers();
                broadcast.writeInt(-1);
                broadcast.writeUTF("loadJson");
                broadcast.writeUTF(file);
                broadcast.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
