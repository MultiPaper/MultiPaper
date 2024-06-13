package io.multipaper.databasemessagingprotocol.messages;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class MessageHandler<T extends Message> extends SimpleChannelInboundHandler<T> {

    /**
     * Called when a message is received.
     * @param message The message that has been received
     * @return true if we have handled it, or false if the normal handler
     *         should handle it
     */
    public boolean onMessage(T message) {
        return false;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T message) {
        if (!onMessage(message)) {
            try {
                message.handle(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
