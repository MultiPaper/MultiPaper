package puregero.multipaper.server.handlers;

import puregero.multipaper.server.DataOutputSender;
import puregero.multipaper.server.ServerConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

public class GetEntityCounterStartValue implements Handler {
    private final HashMap<String, Integer> assignedStartValues = new HashMap<>();
    private int lastStartValue = 0;

    @Override
    public void handle(ServerConnection connection, DataInputStream in, DataOutputSender out) throws IOException {
        out.writeUTF("getEntityCounterStartValue");
        out.writeInt(assignedStartValues.computeIfAbsent(connection.getBungeeCordName(), key -> lastStartValue++));
        out.send();
    }
}
