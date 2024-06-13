package io.multipaper.databasemessagingprotocol;

import io.multipaper.databasemessagingprotocol.messages.Message;
import io.multipaper.databasemessagingprotocol.messages.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder<T extends Message<?>> extends MessageToByteEncoder<Message<?>> {

    private final Protocol<T> protocol;

    public MessageEncoder(Protocol<T> protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message<?> message, ByteBuf byteBuf) {
        ExtendedByteBuf extendedByteBuf = new ExtendedByteBuf(byteBuf);
        extendedByteBuf.writeVarInt(this.protocol.getMessageId((T) message));
        message.write(extendedByteBuf);
    }
}
