package puregero.multipaper.databasemessagingprotocol.messages.serverbound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class ChunkSubscribersSyncMessage extends ServerBoundMessage {

    public final String world;
    public final int cx;
    public final int cz;
    public final String owner;
    public final String[] subscribers;

    public ChunkSubscribersSyncMessage(String world, int cx, int cz, String owner, String[] subscribers) {
        this.world = world;
        this.cx = cx;
        this.cz = cz;
        this.owner = owner;
        this.subscribers = subscribers;
    }

    public ChunkSubscribersSyncMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        cx = byteBuf.readInt();
        cz = byteBuf.readInt();
        owner = byteBuf.readString();
        subscribers = new String[byteBuf.readVarInt()];
        for (int i = 0; i < subscribers.length; i ++) {
            subscribers[i] = byteBuf.readString();
        }
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(world);
        byteBuf.writeInt(cx);
        byteBuf.writeInt(cz);
        byteBuf.writeString(owner);
        byteBuf.writeVarInt(subscribers.length);
        for (String subscriber : subscribers) {
            byteBuf.writeString(subscriber);
        }
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
