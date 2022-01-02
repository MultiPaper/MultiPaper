package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WriteTickTimeHandler implements Handler {
    private Map<String, Long> lastUpdates = new ConcurrentHashMap<>();

    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        long tickTime = in.readLong();
        float tps = in.readFloat();

        connection.getTimer().append(tickTime);
        connection.setTps(tps);
        
        if (tickTime == -1) {
            connection.setTps(-1);
        }

        try {
            if (lastUpdates.getOrDefault(connection.getBungeeCordName(), 0L) < System.currentTimeMillis() - 1000 || connection.getTps() == -1) {
                lastUpdates.put(connection.getBungeeCordName(), System.currentTimeMillis());

                DataOutputStream broadcast = connection.broadcastAll();
                broadcast.writeInt(-1);
                broadcast.writeUTF("serverInfo");
                broadcast.writeUTF(connection.getBungeeCordName());
                broadcast.writeInt((int) connection.getTimer().averageInMillis());
                broadcast.writeFloat((float) connection.getTps());
                broadcast.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
