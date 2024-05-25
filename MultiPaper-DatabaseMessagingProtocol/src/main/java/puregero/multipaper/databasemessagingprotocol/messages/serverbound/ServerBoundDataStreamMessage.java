package puregero.multipaper.databasemessagingprotocol.messages.serverbound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class ServerBoundDataStreamMessage extends ServerBoundMessage {
    public final int streamId;
    public final byte[] data;
    private final int offset;
    private final int length;

    public ServerBoundDataStreamMessage(int streamId, byte[] data, int offset, int length) {
        this.streamId = streamId;
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public ServerBoundDataStreamMessage(ExtendedByteBuf byteBuf) {
        streamId = byteBuf.readVarInt();
        data = new byte[byteBuf.readVarInt()];
        byteBuf.readBytes(data);
        offset = 0;
        length = data.length;
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeVarInt(streamId);
        byteBuf.writeVarInt(length);
        byteBuf.writeBytes(data, offset, length);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.getDataStreamManager().handleInboundData(streamId, data);
    }
}
