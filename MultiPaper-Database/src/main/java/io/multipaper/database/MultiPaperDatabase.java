package io.multipaper.database;

import io.multipaper.database.proxy.ProxyServer;
import io.multipaper.database.util.LogToFile;
import io.netty.channel.ChannelFuture;
import io.multipaper.databasemessagingprotocol.MessageBootstrap;
import io.multipaper.databasemessagingprotocol.messages.databasebound.DatabaseBoundMessage;
import io.multipaper.databasemessagingprotocol.messages.databasebound.DatabaseBoundProtocol;
import io.multipaper.databasemessagingprotocol.messages.serverbound.ServerBoundMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.ServerBoundProtocol;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class MultiPaperDatabase extends MessageBootstrap<DatabaseBoundMessage, ServerBoundMessage> {
    public static final int DEFAULT_PORT = 35353;
    public static final String SECRET = UUID.randomUUID().toString();

    public static void main(String[] args) throws InterruptedException {
        DAEMON = false;

        String address = null;
        int port = DEFAULT_PORT;

        if ("true".equalsIgnoreCase(System.getProperty("logging.enabled", "true"))) {
            LogToFile.init(); // TODO Use log4j instead
        }

        if (args.length > 0) {
            if (args[0].contains(":")) {
                address = args[0].substring(0, args[0].indexOf(':'));
                args[0] = args[0].substring(args[0].indexOf(':') + 1);
            }
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Usage: java -jar MultiPaperServer.jar <[address:]port> [proxy port]");
                System.exit(1);
            }
        }

        if (args.length > 1) {
            try {
                ProxyServer.openServer(Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                System.err.println("Usage: java -jar MultiPaperServer.jar <[address:]port> [proxy port]");
                System.exit(1);
            }
        }

        new MultiPaperDatabase(address, port);

        if (new CommandLineInput().run()) {
            System.out.println("Exiting safely...");

            awaitAsyncTasks();

            System.out.println("Shutting down event loop...");
            eventLoopGroup.shutdownGracefully().await();

            awaitAsyncTasks();

            System.exit(0);
        }
    }

    private static void awaitAsyncTasks() {
        System.out.println("Waiting for async tasks...");
        if (!ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS)) {
            System.out.println("Some tasks are taking a long time to complete.");
            System.out.println("Thread list:");
            ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
            for (ThreadInfo thread : threads) {
                System.out.println(thread);
            }

            ForkJoinPool.commonPool().awaitQuiescence(1, TimeUnit.HOURS);
        }
    }

    public MultiPaperDatabase(int port) {
        this(null, port);
    }

    public MultiPaperDatabase(String address, int port) {
        super(new DatabaseBoundProtocol(), new ServerBoundProtocol(), (channel, bootstrap) -> channel.pipeline().addLast(new ServerConnection(channel, bootstrap.getInboundProtocol().getProtocolHash(), bootstrap.getOutboundProtocol().getProtocolHash())));

        ChannelFuture future;
        if (address == null) {
            future = this.listenOn(port);
        } else {
            future = this.listenOn(address, port);
        }

        future.addListener(f -> {
            if (f.cause() != null) {
                f.cause().printStackTrace();
            } else {
                System.out.println("[MultiPaperDatabase] Listening on " + (address == null ? "0.0.0.0" : address) + ":" + port);
            }
        });
    }
}
