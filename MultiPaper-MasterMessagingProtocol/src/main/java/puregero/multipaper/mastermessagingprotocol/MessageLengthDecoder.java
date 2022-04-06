package puregero.multipaper.mastermessagingprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageLengthDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        int length = 0;
        int j = 0;

        byte b0;

        byteBuf.markReaderIndex();

        do {
            if (byteBuf.readableBytes() < 1) {
                byteBuf.resetReaderIndex();
                return;
            }

            b0 = byteBuf.readByte();
            length |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);

        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            return;
        }

        list.add(byteBuf.readBytes(length));
    }
}
