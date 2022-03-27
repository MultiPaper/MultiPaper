package puregero.multipaper.mastermessagingprotocol.messages;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public abstract class Message<T extends MessageHandler<? extends Message<?>>> {

    private int transactionId;

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public abstract void write(ExtendedByteBuf byteBuf);

    public abstract void handle(T handler);
}
