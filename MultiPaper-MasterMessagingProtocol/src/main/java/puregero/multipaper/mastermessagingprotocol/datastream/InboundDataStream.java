package puregero.multipaper.mastermessagingprotocol.datastream;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InboundDataStream {

    private final DataStreamManager<?> manager;

    private final Channel channel;

    private final int streamId;

    List<Consumer<byte[]>> dataHandlers = new ArrayList<>();

    List<Runnable> closeHandlers = new ArrayList<>();

    InboundDataStream(DataStreamManager<?> manager, Channel channel, int streamId) {
        this.manager = manager;
        this.channel = channel;
        this.streamId = streamId;
        manager.inboundDataStreams.put(streamId, this);
    }

    public int getStreamId() {
        return streamId;
    }

    public InboundDataStream onData(Consumer<byte[]> onData) {
        dataHandlers.add(onData);
        return this;
    }

    public InboundDataStream onClose(Runnable onClose) {
        closeHandlers.add(onClose);
        return this;
    }

    public InputStream createInputStream() throws IOException {
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream(inputStream);
        onData(data -> {
            try {
                outputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        onClose(() -> {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return inputStream;
    }

    public ChannelFuture copyToAsync(OutputStream out) {
        ChannelPromise promise = new DefaultChannelPromise(channel);
        onData(data -> {
            if (promise.isDone()) return;
            try {
                out.write(data);
            } catch (IOException e) {
                promise.setFailure(e);
            }
        });
        onClose(() -> {
            if (promise.isDone()) return;
            try {
                out.close();
                promise.setSuccess();
            } catch (IOException e) {
                promise.setFailure(e);
            }
        });
        return promise;
    }

    void doData(byte[] data) {
        if (data.length == 0) {
            doClose();
        } else {
            dataHandlers.forEach(handler -> handler.accept(data));
        }
    }

    void doClose() {
        closeHandlers.forEach(Runnable::run);
        manager.inboundDataStreams.remove(streamId, this);
    }
}
