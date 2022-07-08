package puregero.multipaper.mastermessagingprotocol.messages.serverbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class IntegerPairMessageReply extends ServerBoundMessage {

    public final int x;
    public final int y;

    public IntegerPairMessageReply(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public IntegerPairMessageReply(ExtendedByteBuf byteBuf) {
        this.x = byteBuf.readInt();
        this.y = byteBuf.readInt();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeInt(this.x);
        byteBuf.writeInt(this.y);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        throw new UnsupportedOperationException("This message can only be handled in a reply");
    }
}
