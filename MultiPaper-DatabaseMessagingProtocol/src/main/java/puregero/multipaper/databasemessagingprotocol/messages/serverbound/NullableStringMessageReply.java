package puregero.multipaper.databasemessagingprotocol.messages.serverbound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class NullableStringMessageReply extends ServerBoundMessage {

    public final String result;

    public NullableStringMessageReply(String result) {
        this.result = result;
    }

    public NullableStringMessageReply(ExtendedByteBuf byteBuf) {
        if (byteBuf.readBoolean()) {
            result = byteBuf.readString();
        } else {
            result = null;
        }
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeBoolean(result != null);
        if (result != null) {
            byteBuf.writeString(result);
        }
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        throw new UnsupportedOperationException("This message can only be handled in a reply");
    }
}
