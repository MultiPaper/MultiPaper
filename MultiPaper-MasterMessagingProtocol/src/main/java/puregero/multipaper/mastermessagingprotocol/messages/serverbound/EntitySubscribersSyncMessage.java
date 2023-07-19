package puregero.multipaper.mastermessagingprotocol.messages.serverbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class EntitySubscribersSyncMessage extends ServerBoundMessage {

    public final String world;

    public final int cx;
    public final int cz;

    public final String[] subscribers;

    public EntitySubscribersSyncMessage(String world, int cx, int cz, String[] subscribers) {
        this.world = world;
        this.cx = cx;
        this.cz = cz;
        this.subscribers = subscribers;
    }

    public EntitySubscribersSyncMessage(ExtendedByteBuf byteBuf) {
        world = byteBuf.readString();
        cx = byteBuf.readInt();
        cz = byteBuf.readInt();
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
