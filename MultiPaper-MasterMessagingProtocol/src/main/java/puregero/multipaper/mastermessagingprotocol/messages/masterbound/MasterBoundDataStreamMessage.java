package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class MasterBoundDataStreamMessage extends MasterBoundMessage {

    public final int streamId;

    public final byte[] data;

    private final int offset;
    private final int length;

    public MasterBoundDataStreamMessage(int streamId, byte[] data, int offset, int length) {
        this.streamId = streamId;
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public MasterBoundDataStreamMessage(ExtendedByteBuf byteBuf) {
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
    public void handle(MasterBoundMessageHandler handler) {
        handler.getDataStreamManager().handleInboundData(streamId, data);
    }
}
