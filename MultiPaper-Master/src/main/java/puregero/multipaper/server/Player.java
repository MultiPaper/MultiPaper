package puregero.multipaper.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class Player {
    private final UUID uuid;
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public Player(UUID uuid, String world, double x, double y, double z, float yaw, float pitch) {
        this.uuid = uuid;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static Player read(DataInputStream in) throws IOException {
        return new Player(
            new UUID(in.readLong(), in.readLong()),
            in.readUTF(),
            in.readDouble(),
            in.readDouble(),
            in.readDouble(),
            in.readFloat(),
            in.readFloat()
        );
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
        out.writeUTF(world);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeFloat(yaw);
        out.writeFloat(pitch);
    }

    public UUID getUuid() {
        return uuid;
    }
}
