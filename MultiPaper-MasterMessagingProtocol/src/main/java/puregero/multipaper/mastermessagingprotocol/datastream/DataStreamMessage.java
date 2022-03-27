package puregero.multipaper.mastermessagingprotocol.datastream;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;
import puregero.multipaper.mastermessagingprotocol.messages.Message;
import puregero.multipaper.mastermessagingprotocol.messages.MessageHandler;

public class DataStreamMessage<T extends MessageHandler<? extends Message<?>>> extends Message<T> {
    public final int streamId;
    public final byte[] data;
    private final int offset;
    private final int length;

    public DataStreamMessage(int streamId, byte[] data, int offset, int length) {
        this.streamId = streamId;
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public DataStreamMessage(ExtendedByteBuf byteBuf) {
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
    public void handle(T handler) {
        handler.getDataStreamManager().handleInboundData(streamId, data);
    }
}
