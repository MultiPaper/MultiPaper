package puregero.multipaper.server.proxy;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
public class HelloPacket {

    private final int protocolVersion;
    public String host;
    private final short port;
    private final byte nextState;

    public HelloPacket(int protocolVersion, String host, short port, byte nextState) {
        this.protocolVersion = protocolVersion;
        this.host = host;
        this.port = port;
        this.nextState = nextState;
    }

    public static HelloPacket read(ByteBuffer buffer) {
        int length = buffer.get() & 0xFF;
        if (length == 0xFE) {
            // Legacy ping packet
            return null;
        }

        if (length > 127) {
            // This hello packet is very long - probably already contains BungeeCord details
            return null;
        }

        int packetId = readVarInt(buffer);

        if (packetId == 0) {
            int protocolVersion = readVarInt(buffer);
            String host = readString(buffer);
            short port = buffer.getShort();
            byte nextState = buffer.get();
            return new HelloPacket(protocolVersion, host, port, nextState);
        }

        return null;
    }

    public void write(ByteBuffer buffer) {
        int lengthPosition = buffer.position();
        buffer.position(lengthPosition + 1);

        writeVarInt(buffer, 0);
        writeVarInt(buffer, protocolVersion);
        writeVarInt(buffer, host.getBytes(StandardCharsets.UTF_8).length);
        buffer.put(host.getBytes(StandardCharsets.UTF_8));
        buffer.putShort(port);
        buffer.put(nextState);

        int packetLength = buffer.position() - lengthPosition - 1;
        if (packetLength > 127) {
            log.info("HelloPacket written length is " + packetLength + "!!!");
        }

        buffer.put(lengthPosition, (byte) packetLength);
    }

    private static int readVarInt(ByteBuffer buffer) {
        int value = 0;
        int length = 0;
        byte currentByte;

        do {
            currentByte = buffer.get();
            value |= (currentByte & 0x7F) << (length * 7);

            length += 1;
            if (length > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((currentByte & 0x80) == 0x80);

        return value;
    }

    private static void writeVarInt(ByteBuffer buffer, int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                buffer.put((byte) value);
                return;
            }

            buffer.put((byte) ((value & 0x7F) | 0x80));
            value >>>= 7;
        }
    }

    private static String readString(ByteBuffer buffer) {
        byte[] bytes = new byte[readVarInt(buffer)];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String sanitizeAddress(InetSocketAddress addr) {
        String string = addr.getHostString();

        // Remove IPv6 scope if present
        if (addr.getAddress() instanceof Inet6Address) {
            int strip = string.indexOf('%');
            return (strip == -1) ? string : string.substring(0, strip);
        } else {
            return string;
        }
    }
}
