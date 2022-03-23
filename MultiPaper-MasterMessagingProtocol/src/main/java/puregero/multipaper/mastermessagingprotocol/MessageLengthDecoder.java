package puregero.multipaper.mastermessagingprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageLengthDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        int i = byteBuf.readableBytes();
        if (i >= 4) {
            byteBuf.markReaderIndex();

            int length = byteBuf.readInt();

            if (byteBuf.readableBytes() < length) {
                byteBuf.resetReaderIndex();
                return;
            }

            list.add(byteBuf.readBytes(length));
        }
    }
}
