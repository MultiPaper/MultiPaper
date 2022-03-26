package puregero.multipaper.mastermessagingprotocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import puregero.multipaper.mastermessagingprotocol.messages.Message;
import puregero.multipaper.mastermessagingprotocol.messages.Protocol;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

public class MessageBootstrap<I extends Message<?>, O extends Message<?>> extends ChannelInitializer<SocketChannel> {

    public static int MAX_BYTES_PER_READ = Integer.getInteger("max_bytes_per_read", 256 * 1024 * 1024);
    public static boolean DAEMON = true;
    private static final ThreadFactory eventLoopThreadFactory = new ThreadFactory() {
        private int counter = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "MultiPaper-Netty-" + (++counter));
            thread.setDaemon(DAEMON);
            return thread;
        }
    };

    private static EventLoopGroup eventLoopGroup;
    private static Class<? extends SocketChannel> socketChannelClass;
    private static Class<? extends ServerSocketChannel> serverSocketChannelClass;

    private final Protocol<I> inboundProtocol;
    private final Protocol<O> outboundProtocol;
    private final Consumer<SocketChannel> setupChannel;

    public static EventLoopGroup getEventLoopGroup() {
        if (eventLoopGroup == null) {
            if (Epoll.isAvailable()) {
                eventLoopGroup = new EpollEventLoopGroup(Integer.getInteger("multipaper.netty.threads", 0), eventLoopThreadFactory);
                socketChannelClass = EpollSocketChannel.class;
                serverSocketChannelClass = EpollServerSocketChannel.class;
            } else {
                eventLoopGroup = new NioEventLoopGroup(Integer.getInteger("multipaper.netty.threads", 0), eventLoopThreadFactory);
                socketChannelClass = NioSocketChannel.class;
                serverSocketChannelClass = NioServerSocketChannel.class;
            }
        }
        return eventLoopGroup;
    }

    public MessageBootstrap(Protocol<I> inboundProtocol, Protocol<O> outboundProtocol, Consumer<SocketChannel> setupChannel) {
        this.inboundProtocol = inboundProtocol;
        this.outboundProtocol = outboundProtocol;
        this.setupChannel = setupChannel;
    }

    private Bootstrap createBootstrap() {
        return new Bootstrap()
                .group(getEventLoopGroup())
                .channel(socketChannelClass)
                .handler(this)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new DefaultMaxBytesRecvByteBufAllocator(MAX_BYTES_PER_READ, MAX_BYTES_PER_READ))
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    private ServerBootstrap createServerBootstrap() {
        return new ServerBootstrap()
                .group(getEventLoopGroup())
                .channel(serverSocketChannelClass)
                .childHandler(this)
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new DefaultMaxBytesRecvByteBufAllocator(MAX_BYTES_PER_READ, MAX_BYTES_PER_READ))
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    public ChannelFuture connectTo(String address, int port) {
        return createBootstrap().connect(address, port);
    }

    public ChannelFuture listenOn(String address, int port) {
        return createServerBootstrap().bind(address, port);
    }

    public ChannelFuture listenOn(int port) {
        return createServerBootstrap().bind(port);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(new MessageLengthEncoder());
        socketChannel.pipeline().addLast(new MessageLengthDecoder());
        socketChannel.pipeline().addLast(new MessageEncoder<>(this.outboundProtocol));
        socketChannel.pipeline().addLast(new MessageDecoder<>(this.inboundProtocol));
        setupChannel.accept(socketChannel);
    }

}
