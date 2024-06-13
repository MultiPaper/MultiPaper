package io.multipaper.databasemessagingprotocol.messages;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public abstract class Message<T extends MessageHandler<? extends Message<?>>> {

    public abstract void write(ExtendedByteBuf byteBuf);

    public abstract void handle(T handler);

}
