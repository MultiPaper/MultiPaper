package puregero.multipaper.databasemessagingprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import puregero.multipaper.databasemessagingprotocol.messages.Message;
import puregero.multipaper.databasemessagingprotocol.messages.Protocol;

public class MessageEncoder<T extends Message<?>> extends MessageToByteEncoder<Message<?>> {

    private final Protocol<T> protocol;

    public MessageEncoder(Protocol<T> protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message<?> message, ByteBuf byteBuf) {
        ExtendedByteBuf extendedByteBuf = new ExtendedByteBuf(byteBuf);
        extendedByteBuf.writeVarInt(message.getTransactionId());
        extendedByteBuf.writeVarInt(protocol.getMessageId((T) message));
        message.write(extendedByteBuf);
    }
}
