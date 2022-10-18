package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ChunkKey;
import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class RequestChunkOwnershipMessage extends MasterBoundMessage {

    public final String world;
    public final ChunkKey[] chunks;
    public boolean force;

    public RequestChunkOwnershipMessage(String world, ChunkKey[] chunks) {
        this.world = world;
        this.chunks = chunks;
        this.force = false;
    }

    public RequestChunkOwnershipMessage(String world, ChunkKey[] chunks, boolean force) {
        this.world = world;
        this.chunks = chunks;
        this.force = force;
    }

    public RequestChunkOwnershipMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        chunks = new ChunkKey[byteBuf.readVarInt()];
        for (int i = 0; i < chunks.length; i++) {
            chunks[i] = byteBuf.readChunkKey();
        }
        force = byteBuf.readBoolean();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeVarInt(chunks.length);
        for (ChunkKey key : chunks) {
            byteBuf.writeChunkKey(key);
        }
        byteBuf.writeBoolean(force);
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }
}
