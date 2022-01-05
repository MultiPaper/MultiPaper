package puregero.multipaper.server.proxy;

import puregero.multipaper.server.ServerConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ProxyServer extends Thread {

    public static final int BUFFER_SIZE = Integer.parseInt(System.getProperty("proxyserver.buffersize", "32768"));
    public static final int WORKER_THREADS = Integer.parseInt(System.getProperty("proxyserver.workerthreads", Integer.toString(Runtime.getRuntime().availableProcessors())));

    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private Selector[] workerSelectors;
    private int workerSelectorsIndex = 0;

    public static void openServer(int port) {
        try {
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress("0.0.0.0", port));

            System.out.println("[ProxyServer] Listening on " + serverChannel.getLocalAddress());

            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            ProxyServer acceptThread = new ProxyServer(serverChannel, selector, "ProxyServer-Accept");

            acceptThread.workerSelectors = new Selector[Math.max(1, WORKER_THREADS)];
            for (int i = 0; i < acceptThread.workerSelectors.length; i ++) {
                acceptThread.workerSelectors[i] = Selector.open();
                new ProxyServer(serverChannel, acceptThread.workerSelectors[i], "ProxyServer-Worker #" + (i + 1)).start();
            }

            acceptThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ProxyServer(ServerSocketChannel serverChannel, Selector selector, String name) {
        this.serverChannel = serverChannel;
        this.selector = selector;
        setName(name);
    }

    @Override
    public void run() {
        try {
            while (serverChannel.isOpen() && selector.isOpen() && !Thread.currentThread().isInterrupted()) {
                selector.select();

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) {
                        if (key.attachment() instanceof ProxiedConnection) {
                            ((ProxiedConnection) key.attachment()).close();
                        }

                        continue;
                    }

                    if (key.isAcceptable()) {
                        accept(key);
                    }

                    if (key.isValid() && key.isWritable()) {
                        ((ProxiedConnection) key.attachment()).write(key);
                    }

                    if (key.isValid() && key.isReadable()) {
                        ((ProxiedConnection) key.attachment()).read(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void accept(SelectionKey key) {
        SocketChannel socketChannel = null;
        SocketChannel destinationChannel = null;

        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            socketChannel = serverSocketChannel.accept();

            if (socketChannel == null) {
                return;
            }

            socketChannel.configureBlocking(false);

            SocketAddress destinationAddress = getSuitableServer();

            if (destinationAddress == null) {
                System.out.println("No available servers for " + socketChannel.getRemoteAddress() + " to connect to");
                socketChannel.close();
                return;
            }

            destinationChannel = SocketChannel.open(destinationAddress);
            destinationChannel.configureBlocking(false);

            ProxiedConnection destinationConnection = new ProxiedConnection(destinationChannel, socketChannel);
            destinationConnection.helloPacket = false; // No hello packet for the destination

            ProxiedConnection sourceConnection = new ProxiedConnection(socketChannel, destinationChannel);

            sourceConnection.setWriteBuffer(destinationConnection.getReadBuffer());
            destinationConnection.setWriteBuffer(sourceConnection.getReadBuffer());

            Selector workerSelector = workerSelectors[workerSelectorsIndex = (workerSelectorsIndex + 1) % workerSelectors.length];
            destinationChannel.register(workerSelector, SelectionKey.OP_READ, destinationConnection);
            socketChannel.register(workerSelector, SelectionKey.OP_READ, sourceConnection);
            workerSelector.wakeup();

            System.out.println(socketChannel.getRemoteAddress() + " has connected to " + destinationChannel.getRemoteAddress());
        } catch (IOException e) {
            e.printStackTrace();

            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (destinationChannel != null) {
                try {
                    destinationChannel.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private SocketAddress getSuitableServer() {
        ServerConnection bestServer = null;
        long lowestTickTime = Long.MAX_VALUE;

        for (ServerConnection connection : ServerConnection.getConnections()) {
            if (connection != null && connection.isOnline() && connection.getPort() > 0 && connection.getTimer().averageInMillis() < lowestTickTime) {
                lowestTickTime = connection.getTimer().averageInMillis();
                bestServer = connection;
            }
        }

        if (bestServer != null) {
            return new InetSocketAddress(((InetSocketAddress) bestServer.getAddress()).getAddress(), bestServer.getPort());
        }

        return null;
    }

}
