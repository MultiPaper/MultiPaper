package puregero.multipaper.mastermessagingprotocol.datastream;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OutboundDataStream {

    private final DataStreamManager<?> manager;
    private final Channel channel;
    private final int streamId;

    OutboundDataStream(DataStreamManager<?> manager, Channel channel, int streamId) {
        this.manager = manager;
        this.channel = channel;
        this.streamId = streamId;
    }

    public int getStreamId() {
        return streamId;
    }

    public ChannelFuture send(byte[] bytes, int offset, int length) {
        if (bytes.length > 0) {
            return channel.writeAndFlush(manager.createDataStreamMessage(streamId, bytes, offset, length));
        } else {
            return new DefaultChannelPromise(channel).setSuccess();
        }
    }

    public ChannelFuture send(byte[] bytes) {
        return send(bytes, 0, bytes.length);
    }

    public ChannelFuture endStream() {
        return channel.writeAndFlush(manager.createDataStreamMessage(streamId, new byte[0], 0, 0));
    }

    public OutputStream createOutputSteam() {
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                write(new byte[] {(byte) b});
            }

            @Override
            public void write(byte[] b, int o, int l) throws IOException {
                try {
                    send(b, o, l).await();
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
            }

            @Override
            public void close() {
                endStream();
            }
        };
    }

    public ChannelFuture copyFromAsync(InputStream in) {
        ChannelPromise promise = new DefaultChannelPromise(channel);
        byte[] buffer = new byte[64 * 1024];
        doCopyFrom(in, promise, buffer);
        return promise;
    }

    private void doCopyFrom(InputStream in, ChannelPromise promise, byte[] buffer) {
        try {
            int length = in.read(buffer);
            if (length <= 0) {
                in.close();
                endStream().addListener(future -> promise.setSuccess());
                return;
            }

            send(buffer, 0, length).addListener(future -> doCopyFrom(in, promise, buffer));
        } catch (IOException e) {
            promise.setFailure(e);
        }
    }
}
