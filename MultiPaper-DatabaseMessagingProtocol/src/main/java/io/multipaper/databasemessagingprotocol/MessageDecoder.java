package io.multipaper.databasemessagingprotocol;

import io.multipaper.databasemessagingprotocol.messages.Message;
import io.multipaper.databasemessagingprotocol.messages.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder<T extends Message<?>> extends ByteToMessageDecoder {

    private final Protocol<T> protocol;

    public MessageDecoder(Protocol<T> protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        ExtendedByteBuf extendedByteBuf = new ExtendedByteBuf(byteBuf);
        int messageId = extendedByteBuf.readVarInt();
        list.add(this.protocol.getDeserializer(messageId).apply(extendedByteBuf));
    }
}
