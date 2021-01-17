package puregero.multipaper.server.handlers;

import puregero.multipaper.server.*;
import puregero.multipaper.server.locks.PlayerLock;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReadPlayerHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String uuid = in.readUTF();

        AtomicBoolean hasWritten = new AtomicBoolean(false);

        Worker.runAsync(() -> {
            String lockHolder = PlayerLock.getLockHolder(uuid);

            if (lockHolder == null || lockHolder.equals(connection.getBungeeCordName()) || !ServerConnection.isAlive(lockHolder)) {
                readAndSendPlayerData(uuid, out);
            } else {
                try {
                    DataOutputSender sender = ServerConnection.getConnection(lockHolder).buffer();
                    sender.writeUTF("savePlayer");
                    sender.writeUTF(uuid);
                    sender.send(dataInputStream -> {
                        Worker.runAsync(() -> {
                            try {
                                Thread.sleep(1); // Ensure write worker runs first
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (hasWritten.compareAndSet(false, true)) {
                                readAndSendPlayerData(uuid, out);
                            }
                        });
                    });

                    Scheduler.schedule(() -> {
                        if (hasWritten.compareAndSet(false, true)) {
                            System.out.println("Timed out waiting for " + lockHolder + ", sending stats to " + connection.getBungeeCordName() + " anyway");
                            readAndSendPlayerData(uuid, out);
                        }
                    }, 15000);
                } catch (Exception e) {
                    e.printStackTrace();
                    readAndSendPlayerData(uuid, out);
                }
            }
        });
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
