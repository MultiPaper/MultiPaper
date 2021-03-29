package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.Player;
import puregero.multipaper.server.ServerConnection;
import puregero.multipaper.server.Worker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerUpdateHandler implements Handler {

    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        boolean add = in.readBoolean();
        Player player = Player.read(in);
        
        connection.getPlayers().removeIf(p -> p.getUuid().equals(player.getUuid()));
        
        if (add) {
            connection.getPlayers().add(player);
        }

        Worker.runAsync(() -> {
            try {
                DataOutputStream broadcast = connection.broadcastOthers();
                broadcast.writeInt(-1);
                broadcast.writeUTF("playerUpdate");
                broadcast.writeUTF(connection.getBungeeCordName());
                broadcast.writeBoolean(add);
                player.write(broadcast);
                broadcast.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
