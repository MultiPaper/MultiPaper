package puregero.multipaper.server.handlers;

import puregero.multipaper.server.*;
import puregero.multipaper.server.handlers.Handler;
import puregero.multipaper.server.locks.AdvancementsLock;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReadAdvancementsHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String uuid = in.readUTF();

        AtomicBoolean hasWritten = new AtomicBoolean(false);

        Worker.runAsync(() -> {
            String lockHolder = AdvancementsLock.getLockHolder(uuid);

            if (lockHolder == null || lockHolder.equals(connection.getBungeeCordName()) || !ServerConnection.isAlive(lockHolder)) {
                readAndSendAdvancementsData(uuid, out);
            } else {
                try {
                    DataOutputSender sender = ServerConnection.getConnection(lockHolder).buffer();
                    sender.writeUTF("saveAdvancements");
                    sender.writeUTF(uuid);
                    sender.send(dataInputStream -> {
                        Worker.runAsync(() -> {
                            try {
                                Thread.sleep(1); // Ensure write worker runs first
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (hasWritten.compareAndSet(false, true)) {
                                readAndSendAdvancementsData(uuid, out);
                            }
                        });
                    });

                    Scheduler.schedule(() -> {
                        if (hasWritten.compareAndSet(false, true)) {
                            System.out.println("Timed out waiting for " + lockHolder + ", sending stats to " + connection.getBungeeCordName() + " anyway");
                            readAndSendAdvancementsData(uuid, out);
                        }
                    }, 15000);
                } catch (Exception e) {
                    e.printStackTrace();
                    readAndSendAdvancementsData(uuid, out);
                }
            }
        });
    }

    private void readAndSendAdvancementsData(String uuid, DataOutputSender out) {
        try {
            byte[] b = FileLocker.readBytes(new File("world/advancements", uuid + ".json"));
            out.writeUTF("advancementsData");
            out.writeInt(b.length);
            out.write(b);
            out.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
