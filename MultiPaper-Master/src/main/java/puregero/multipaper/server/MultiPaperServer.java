package puregero.multipaper.server;

import io.netty.channel.ChannelFuture;
import puregero.multipaper.mastermessagingprotocol.MessageBootstrap;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.MasterBoundMessage;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.MasterBoundProtocol;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ServerBoundMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ServerBoundProtocol;
import puregero.multipaper.server.proxy.ProxyServer;

import java.io.IOException;
import java.util.UUID;

public class MultiPaperServer extends MessageBootstrap<MasterBoundMessage, ServerBoundMessage> {
    public static final int DEFAULT_PORT = 35353;
    public static final String SECRET = UUID.randomUUID().toString();

    public static void main(String[] args) throws IOException {
        String address = null;
        int port = DEFAULT_PORT;

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

        new MultiPaperServer(address, port);

        new CommandLineInput().run();
    }

    public MultiPaperServer(int port) {
        this(null, port);
    }

    public MultiPaperServer(String address, int port) {
        super(new MasterBoundProtocol(), new ServerBoundProtocol(), channel -> channel.pipeline().addLast(new ServerConnection(channel)));

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
                System.out.println("[MultiPaperMaster] Listening on " + (address == null ? "0.0.0.0" : address) + ":" + port);
            }
        });
    }
}
