package puregero.multipaper.databasemessagingprotocol.messages.serverbound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class DataStreamMessageReply extends ServerBoundMessage {

    public final int streamId;

    public DataStreamMessageReply(int streamId) {
        this.streamId = streamId;
    }

    public DataStreamMessageReply(ExtendedByteBuf byteBuf) {
        streamId = byteBuf.readVarInt();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeVarInt(streamId);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        throw new UnsupportedOperationException("This message can only be handled in a reply");
    }
}
