package puregero.multipaper.mastermessagingprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageLengthEncoder extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBufDest) {
        byteBufDest.writeInt(byteBuf.readableBytes());
        byteBufDest.writeBytes(byteBuf);
    }
}
