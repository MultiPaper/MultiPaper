package puregero.multipaper.server;

import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import puregero.multipaper.mastermessagingprotocol.MessageBootstrap;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.MasterBoundMessage;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.MasterBoundProtocol;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ServerBoundMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.ServerBoundProtocol;
import puregero.multipaper.server.proxy.ProxyServer;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class MultiPaperServer extends MessageBootstrap<MasterBoundMessage, ServerBoundMessage> {

    public static final int DEFAULT_PORT = 35353;

    public static final String SECRET = UUID.randomUUID().toString();

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
                log.info("Listening on " + (address == null ? "0.0.0.0" : address) + ":" + port);
            }
        });
    }
}
