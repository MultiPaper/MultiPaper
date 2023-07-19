package puregero.multipaper.server.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ProxiedConnection {

    private final SocketChannel socketChannel;
    private final SocketChannel destinationChannel;

    private final ByteBuffer readBuffer = ByteBuffer.allocate(ProxyServer.BUFFER_SIZE);
    private ByteBuffer writeBuffer;

    public boolean helloPacket = true;

    public ProxiedConnection(SocketChannel socketChannel, SocketChannel destinationChannel) {
        this.socketChannel = socketChannel;
        this.destinationChannel = destinationChannel;

        readBuffer.limit(0);
    }

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public void setWriteBuffer(ByteBuffer writeBuffer) {
        this.writeBuffer = writeBuffer;
    }

    public void write(SelectionKey key) {
        if (writeBuffer.hasRemaining()) {
            try {
                socketChannel.write(writeBuffer);
            } catch (IOException e) {
                close();
                return;
            }

            SelectionKey destinationKey = destinationChannel.keyFor(key.selector());
            if (!writeBuffer.hasRemaining()) {
                // We wrote all the data, stop listening to the write op and start listening to the read op
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                destinationKey.interestOps(destinationKey.interestOps() | SelectionKey.OP_READ);
                // Notify the other connection that the buffer has been emptied
                ((ProxiedConnection) destinationKey.attachment()).read(destinationKey);
            } else {
                // We didn't manage to write all of the data, tell us when we can write again and ignore when we can read
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                destinationKey.interestOps(destinationKey.interestOps() & ~SelectionKey.OP_READ);
            }
        }
    }

    public void read(SelectionKey key) {
        if (!readBuffer.hasRemaining()) {
            readBuffer.position(0);
            readBuffer.limit(readBuffer.capacity());

            try {
                if (socketChannel.read(readBuffer) == -1) {
                    close();
                }
            } catch (IOException e) {
                close();
                return;
            }

            readBuffer.limit(readBuffer.position());
            readBuffer.position(0);

            // Notify the other connection that the buffer has been filled
            if (readBuffer.hasRemaining()) {
                if (helloPacket) {
                    rewriteHelloPacket(readBuffer);
                }

                SelectionKey destinationKey = destinationChannel.keyFor(key.selector());
                ((ProxiedConnection) destinationKey.attachment()).write(destinationKey);
            }
        }
    }

    private void rewriteHelloPacket(ByteBuffer buffer) {
        helloPacket = false;
        byte[] extraData = null;

        try {
            HelloPacket helloPacket = HelloPacket.read(buffer);

            if (helloPacket != null) {
                if (helloPacket.host.indexOf(0) < 0) {
                    // HelloPacket doesn't already contain bungeecord information, add it
                    helloPacket.host += "\00" + HelloPacket.sanitizeAddress((InetSocketAddress) socketChannel.getRemoteAddress());
                }

                if (buffer.hasRemaining()) {
                    extraData = new byte[buffer.remaining()];
                    buffer.get(extraData);
                }

                buffer.limit(buffer.capacity());
                buffer.position(0);

                helloPacket.write(buffer);

                if (extraData != null) {
                    buffer.put(extraData);
                }

                buffer.limit(buffer.position());
                buffer.position(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        buffer.position(0);
    }

    public void close() {
        try {
            if (socketChannel.isOpen()) {
                socketChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (destinationChannel.isOpen()) {
                destinationChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
