package puregero.multipaper.mastermessagingprotocol.datastream;

import io.netty.channel.Channel;
import puregero.multipaper.mastermessagingprotocol.messages.Message;
import puregero.multipaper.mastermessagingprotocol.messages.MessageHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
        InboundDataStream dataStream = inboundDataStreams.get(streamId);

        if (dataStream == null) {
            throw new IllegalArgumentException("Unknown data stream with streamId=" + streamId);
        }

        dataStream.doData(data);
    }

    public T createDataStreamMessage(int streamId, byte[] data, int offset, int length) {
        return messageHandler.createDataStreamMessage(streamId, data, offset, length);
    }

}
