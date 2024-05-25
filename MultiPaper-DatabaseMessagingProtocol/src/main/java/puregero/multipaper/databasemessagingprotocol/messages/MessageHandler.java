package puregero.multipaper.databasemessagingprotocol.messages;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import puregero.multipaper.databasemessagingprotocol.datastream.DataStreamManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class MessageHandler<T extends Message> extends SimpleChannelInboundHandler<T> {

    private final Map<Integer, Consumer<T>> callbacks = new ConcurrentHashMap<>();
    private final AtomicInteger transactionId = new AtomicInteger(1);
    private final DataStreamManager<T> dataStreamManager = new DataStreamManager<>(this);

    public DataStreamManager<T> getDataStreamManager() {
        return dataStreamManager;
    }

    public <X extends Message<?>> X createDataStreamMessage(int streamId, byte[] data, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public <X extends Message<?>> X setCallback(X message, Consumer<T> callback) {
        message.setTransactionId(transactionId.incrementAndGet());
        callbacks.put(message.getTransactionId(), callback);
        return message;
    }

    /**
     * Called when a message is received.
     * @param message The message that has been received
     * @return true if we have handled it, or false if the normal handler
     *         should handle it
     */
    public boolean onMessage(T message) {
        return false;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T message) {
        Consumer<T> callback = callbacks.remove(message.getTransactionId());
        if (callback != null) {
            callback.accept(message);
            return;
        }

        if (!onMessage(message)) {
            try {
                message.handle(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
