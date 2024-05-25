package puregero.multipaper.databasemessagingprotocol.messages.serverbound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class ChunkLoadedOnAnotherServerMessage extends ServerBoundMessage {

    public final String server;

    public ChunkLoadedOnAnotherServerMessage(String server) {
        this.server = server;
    }

    public ChunkLoadedOnAnotherServerMessage(ExtendedByteBuf byteBuf) {
        server = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(server);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        throw new UnsupportedOperationException("This message can only be handled in a reply");
    }
}
