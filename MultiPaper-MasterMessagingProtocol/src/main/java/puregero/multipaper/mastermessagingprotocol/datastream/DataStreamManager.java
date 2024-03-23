package puregero.multipaper.mastermessagingprotocol.datastream;

import io.netty.channel.Channel;
import puregero.multipaper.mastermessagingprotocol.messages.Message;
import puregero.multipaper.mastermessagingprotocol.messages.MessageHandler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DataStreamManager<T extends Message> {
    private final AtomicInteger streamIdIncrementer = new AtomicInteger(1);
    final Map<Integer, InboundDataStream> inboundDataStreams = new ConcurrentHashMap<>();
    private final MessageHandler<T> messageHandler;

    public DataStreamManager(MessageHandler<T> messageHandler) {
        this.messageHandler = messageHandler;
    }

    public OutboundDataStream createOutboundDataStream(Channel channel) {
        return new OutboundDataStream(this, channel, streamIdIncrementer.incrementAndGet());
    }

    public InboundDataStream createInboundDataStream(Channel channel, int streamId) {
        return new InboundDataStream(this, channel, streamId);
    }

    public InboundDataStream createInboundDataStream(Channel channel, int streamId, Consumer<byte[]> onData, Runnable onClose) {
        return new InboundDataStream(this, channel, streamId).onData(onData).onClose(onClose);
    }

    public void handleInboundData(int streamId, byte[] data) {
        handleInboundData(streamId, data, 0);
    }

    public void handleInboundData(int streamId, byte[] data, int recursive) {
        InboundDataStream dataStream = inboundDataStreams.get(streamId);

        if (dataStream == null && recursive < 5) {
            CompletableFuture.delayedExecutor(250, TimeUnit.MILLISECONDS).execute(() -> handleInboundData(streamId, data, recursive + 1));
            return;
        }

        if (dataStream == null) {
            throw new IllegalArgumentException("Unknown data stream with streamId=" + streamId);
        }

        dataStream.doData(data);
    }

    public T createDataStreamMessage(int streamId, byte[] data, int offset, int length) {
        return messageHandler.createDataStreamMessage(streamId, data, offset, length);
    }
}
