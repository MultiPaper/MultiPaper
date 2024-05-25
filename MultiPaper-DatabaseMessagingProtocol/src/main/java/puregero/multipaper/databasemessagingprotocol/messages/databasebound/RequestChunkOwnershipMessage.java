package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ChunkKey;
import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class RequestChunkOwnershipMessage extends DatabaseBoundMessage {

    public final String world;
    public final ChunkKey[] chunks;

    public RequestChunkOwnershipMessage(String world, ChunkKey[] chunks) {
        this.world = world;
        this.chunks = chunks;
    }

    public RequestChunkOwnershipMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        chunks = new ChunkKey[byteBuf.readVarInt()];
        for (int i = 0; i < chunks.length; i++) {
            chunks[i] = byteBuf.readChunkKey();
        }
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeVarInt(chunks.length);
        for (ChunkKey key : chunks) {
            byteBuf.writeChunkKey(key);
        }
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
