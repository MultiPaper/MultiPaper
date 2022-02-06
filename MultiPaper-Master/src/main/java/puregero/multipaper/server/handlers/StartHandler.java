package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StartHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        String host = in.readUTF();
        int port = in.readInt();

        if (!host.isEmpty() && !host.equals("0.0.0.0")) {
            System.out.println("Setting " + connection.getBungeeCordName() + "'s host to " + connection.getHost() + " with port " + port);
            connection.setHost(host);
        }

        try {
            System.out.println(connection.getBungeeCordName() + " (" + connection.getHost() + ":" + port + ") has started, broadcasting start to other servers...");
            DataOutputStream broadcast = connection.broadcastOthers();
            broadcast.writeInt(-1);
            broadcast.writeUTF("start");
            broadcast.writeUTF(connection.getHost());
            broadcast.writeInt(port);
            broadcast.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
