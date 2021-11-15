package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public class StartHandler implements Handler {
    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        int port = in.readInt();

        try {
            System.out.println(connection.getBungeeCordName() + " has started, broadcasting start to other servers...");
            DataOutputStream broadcast = connection.broadcastOthers();
            broadcast.writeInt(-1);
            broadcast.writeUTF("start");
            broadcast.writeUTF(((InetSocketAddress) connection.getAddress()).getAddress().getHostAddress());
            broadcast.writeInt(port);
            broadcast.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
