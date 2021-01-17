package puregero.multipaper.server;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class DataOutputSender extends DataOutputStream {
    private final ServerConnection connection;
    private final int id;

    public DataOutputSender(ServerConnection connection) throws IOException {
        this(connection, (int) (Math.random() * Integer.MAX_VALUE));
    }

    public DataOutputSender(ServerConnection connection, int id) throws IOException {
        super(new ByteArrayOutputStream());
        this.connection = connection;
        this.id = id;

        writeInt(id);
    }

    public void send() throws IOException {
        connection.send(((ByteArrayOutputStream) out).toByteArray());
    }

    public void send(@Nullable Consumer<DataInputStream> callback) throws IOException {
        connection.send(((ByteArrayOutputStream) out).toByteArray(), id, callback);
    }
}
