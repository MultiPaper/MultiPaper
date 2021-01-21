package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.Player;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class WriteTickTimeHandler implements Handler {
    private HashMap<String, Long> lastUpdates = new HashMap<>();
    private long lastPlayerList = System.currentTimeMillis();

    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        long tickTime = in.readLong();
        
        connection.getTimer().append(tickTime);
        
        if (tickTime == -1) {
            connection.setTps(-1);
        }

        Worker.runAsync(() -> {
            try {
                if (lastUpdates.getOrDefault(connection.getBungeeCordName(), 0L) < System.currentTimeMillis() - 1000) {
                    lastUpdates.put(connection.getBungeeCordName(), System.currentTimeMillis());

                    DataOutputStream broadcast = connection.broadcastAll();
                    broadcast.writeInt(-1);
                    broadcast.writeUTF("serverInfo");
                    broadcast.writeUTF(connection.getBungeeCordName());
                    broadcast.writeInt((int) connection.getTimer().averageInMillis());
                    broadcast.close();
                }

                if (lastPlayerList < System.currentTimeMillis() - 5 * 1000 || connection.getTps() == -1) {
                    lastPlayerList = System.currentTimeMillis();

                    DataOutputStream broadcast = connection.broadcastAll();
                    broadcast.writeInt(-1);
                    broadcast.writeUTF("playerList");
                    for (ServerConnection otherConnection : ServerConnection.getConnections()) {
                        broadcast.writeUTF(otherConnection.getBungeeCordName());
                        broadcast.writeDouble(otherConnection.getTps());
                        broadcast.writeInt(otherConnection.getPlayers().size());
                        for (Player player : otherConnection.getPlayers()) {
                            player.write(broadcast);
                        }
                    }
                    broadcast.writeUTF(""); // Closing tag
                    broadcast.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
