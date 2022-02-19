package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ReadLevelHandler implements Handler {
    private final HashMap<String, Long> emptyLevelDatLockTimes = new HashMap<>();
    private final HashMap<String, String> emptyLevelDatLocker = new HashMap<>();

    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String world = in.readUTF();
        read(world, connection, out);
    }

    public void read(String world, ServerConnection connection, DataOutputSender out) {
        try {
            try{
                byte[] b = Files.readAllBytes(new File(world, "level.dat").toPath());
                out.writeUTF("levelData");
                out.writeInt(b.length);
                out.write(b);
                out.send();
            } catch (NoSuchFileException e) {
                synchronized (emptyLevelDatLockTimes) {
                    if (!connection.getBungeeCordName().equals(emptyLevelDatLocker.get(world))) {
                        if (emptyLevelDatLockTimes.getOrDefault(world, 0L) > System.currentTimeMillis() - 120 * 1000L) {
                            System.out.println(connection.getBungeeCordName() + " has requested " + world + "'s level.dat, but it is empty. Retying in 10 seconds once " + emptyLevelDatLocker.get(world) + " has generated it...");
                            CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> read(world, connection, out));
                            return;
                        }

                        System.out.println(connection.getBungeeCordName() + " has requested " + world + "'s level.dat, but it is empty. We will assume this server will generate it.");
                        emptyLevelDatLockTimes.put(world, System.currentTimeMillis());
                        emptyLevelDatLocker.put(world, connection.getBungeeCordName());
                    }
                }

                out.writeUTF("levelData");
                out.writeInt(0);
                out.send();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
